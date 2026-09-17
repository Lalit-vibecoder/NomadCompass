package com.example.nomadcompass.ui.screens.planner

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.data.local.entity.ExpenseCategory
import com.example.nomadcompass.domain.model.AttachmentType
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.Expense
import com.example.nomadcompass.domain.model.ItineraryEvent
import com.example.nomadcompass.domain.model.PackingItem
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import com.example.nomadcompass.domain.model.UserProfile
import com.example.nomadcompass.domain.repository.ExpenseRepository
import com.example.nomadcompass.domain.repository.ItineraryRepository
import com.example.nomadcompass.domain.repository.PackingRepository
import com.example.nomadcompass.domain.repository.TripRepository
import com.example.nomadcompass.domain.usecase.CalculateExpenseUseCase
import com.example.nomadcompass.domain.usecase.GetAllCountriesUseCase
import com.example.nomadcompass.domain.usecase.GetProfileUseCase
import com.example.nomadcompass.util.FileStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class WorkspaceTab(val label: String) {
    EXPENSES("Expenses"),
    DOCS("Docs"),
    ITINERARY("Itinerary"),
    PACKING("Packing")
}

data class WorkspaceDetails(
    val attachments: List<TripAttachment>,
    val expenses: List<Expense>,
    val totalSpent: Double,
    val packingItems: List<PackingItem>,
    val itineraryEvents: List<ItineraryEvent> = emptyList(),
)

data class PlannerUiState(
    val trips: List<Trip> = emptyList(),
    val availableCountries: List<Country> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val selectedCca3: String = "JPN",
    val startDate: String = "2026-10-01",
    val endDate: String = "2026-10-31",
    val budgetUsd: String = "2500",
    val notes: String = "Co-working space access & high-speed Wi-Fi",
    val isSaving: Boolean = false,
    val activeWorkspaceTrip: Trip? = null,
    val activeWorkspaceTab: WorkspaceTab = WorkspaceTab.EXPENSES,
    val workspaceAttachments: List<TripAttachment> = emptyList(),
    val workspaceExpenses: List<Expense> = emptyList(),
    val workspacePackingItems: List<PackingItem> = emptyList(),
    val workspaceItineraryEvents: List<ItineraryEvent> = emptyList(),
    val totalSpentHome: Double = 0.0,
    val userCurrencyCode: String = "USD",
    val tripDestinationCurrencyCode: String = "JPY",
    val userProfile: UserProfile? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val packingRepository: PackingRepository,
    private val itineraryRepository: ItineraryRepository,
    private val calculateExpenseUseCase: CalculateExpenseUseCase,
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialCca3: String? = savedStateHandle.get<String>("cca3")

    private val _uiState = MutableStateFlow(
        PlannerUiState(
            selectedCca3 = initialCca3 ?: "JPN",
            isAddDialogOpen = initialCca3 != null
        )
    )

    private val _activeTripId = MutableStateFlow<Int?>(null)

    private val _sortedCountriesFlow = getAllCountriesUseCase().map { countries ->
        withContext(Dispatchers.Default) {
            countries.sortedWith(
                compareByDescending<Country> { it.isFavorite }.thenBy { it.commonName }
            )
        }
    }

    private val _attachmentsFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            tripRepository.getAttachmentsForTrip(tripId)
        } else {
            flowOf(emptyList())
        }
    }

    private val _expensesFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            expenseRepository.getExpensesForTrip(tripId)
        } else {
            flowOf(emptyList())
        }
    }

    private val _totalSpentHomeFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            expenseRepository.getTotalSpentHome(tripId)
        } else {
            flowOf(0.0)
        }
    }

    private val _packingItemsFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            viewModelScope.launch {
                packingRepository.seedDefaultsIfEmpty(tripId)
            }
            packingRepository.getPackingItemsForTrip(tripId)
        } else {
            flowOf(emptyList())
        }
    }

    private val _itineraryEventsFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            itineraryRepository.getItineraryEvents(tripId.toLong())
        } else {
            flowOf(emptyList())
        }
    }

    private val _workspaceDetailsFlow = combine(
        _attachmentsFlow,
        _expensesFlow,
        _totalSpentHomeFlow,
        _packingItemsFlow,
        _itineraryEventsFlow
    ) { attachments, expenses, totalSpent, packingItems, itineraryEvents ->
        WorkspaceDetails(attachments, expenses, totalSpent, packingItems, itineraryEvents)
    }

    val uiState: StateFlow<PlannerUiState> = combine(
        _uiState,
        tripRepository.getAllTrips(),
        _sortedCountriesFlow,
        _workspaceDetailsFlow,
        getProfileUseCase()
    ) { state, trips, sortedCountries, workspaceDetails, profile ->
        val (attachments, expenses, totalSpent, packingItems, itineraryEvents) = workspaceDetails
        val currency = profile?.baseCurrencyCode?.ifBlank { "USD" } ?: "USD"
        val activeTrip = state.activeWorkspaceTrip
        val destCountry = sortedCountries.find { it.cca3.equals(activeTrip?.destinationCca3, ignoreCase = true) }
        val destCurrency = destCountry?.currencyCode?.ifBlank { currency } ?: currency

        val enrichedTrips = trips.map { trip ->
            val country = sortedCountries.find { it.cca3.equals(trip.destinationCca3, ignoreCase = true) }
            if (country != null && (trip.countryName.isBlank() || trip.countryName.equals(trip.destinationCca3, ignoreCase = true) || trip.flagEmoji.isBlank() || trip.flagEmoji == "✈️" || trip.flagUrl.isBlank())) {
                trip.copy(
                    countryName = if (trip.countryName.isBlank() || trip.countryName.equals(trip.destinationCca3, ignoreCase = true)) country.commonName else trip.countryName,
                    flagEmoji = if (trip.flagEmoji.isBlank() || trip.flagEmoji == "✈️") country.flagEmoji else trip.flagEmoji,
                    flagUrl = if (trip.flagUrl.isBlank()) country.flagUrl else trip.flagUrl
                )
            } else {
                trip
            }
        }

        state.copy(
            trips = enrichedTrips,
            availableCountries = sortedCountries,
            workspaceAttachments = attachments,
            workspaceExpenses = expenses,
            workspacePackingItems = packingItems,
            workspaceItineraryEvents = itineraryEvents,
            totalSpentHome = totalSpent,
            userCurrencyCode = currency,
            tripDestinationCurrencyCode = destCurrency,
            userProfile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlannerUiState()
    )

    fun openTripWorkspace(trip: Trip) {
        _activeTripId.value = trip.id
        _uiState.value = _uiState.value.copy(
            activeWorkspaceTrip = trip,
            activeWorkspaceTab = WorkspaceTab.EXPENSES
        )
    }

    fun closeTripWorkspace() {
        _activeTripId.value = null
        _uiState.value = _uiState.value.copy(activeWorkspaceTrip = null)
    }

    fun selectWorkspaceTab(tab: WorkspaceTab) {
        _uiState.value = _uiState.value.copy(activeWorkspaceTab = tab)
    }

    // Itinerary Actions
    fun saveItineraryEvents(events: List<ItineraryEvent>) {
        viewModelScope.launch {
            itineraryRepository.saveItineraryEvents(events)
        }
    }

    fun addItineraryEvent(event: ItineraryEvent) {
        viewModelScope.launch {
            itineraryRepository.saveItineraryEvent(event)
        }
    }

    fun updateItineraryEvent(event: ItineraryEvent) {
        viewModelScope.launch {
            itineraryRepository.updateItineraryEvent(event)
        }
    }

    fun deleteItineraryEvent(id: Long) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryEvent(id)
        }
    }

    // Packing Actions
    fun togglePackingItem(item: PackingItem) {
        viewModelScope.launch {
            packingRepository.togglePackingItem(item)
        }
    }

    fun addPackingItem(name: String) {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            packingRepository.addPackingItem(currentTrip.id, name)
        }
    }

    fun deletePackingItem(id: Long) {
        viewModelScope.launch {
            packingRepository.deletePackingItem(id)
        }
    }

    // Expense Actions
    fun addExpense(
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long = System.currentTimeMillis(),
        paymentMethod: String = "Credit Card",
        receiptPath: String? = null,
    ) {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            val conversionResult = calculateExpenseUseCase.calculateHomeAmount(
                amountLocal = amountLocal,
                currencyCode = currencyCode,
                homeCurrencyCode = uiState.value.userCurrencyCode
            )

            val expense = Expense(
                tripId = currentTrip.id,
                title = title.ifBlank { "Expense" },
                amountLocal = amountLocal,
                currencyCode = currencyCode.uppercase().trim(),
                amountHome = conversionResult.amountHome,
                isUnconverted = conversionResult.isUnconverted,
                category = category,
                date = date,
                notes = notes,
                paymentMethod = paymentMethod,
                receiptPath = receiptPath,
            )

            expenseRepository.addExpense(expense)
        }
    }

    fun updateExpense(
        id: Long,
        title: String,
        amountLocal: Double,
        currencyCode: String,
        category: ExpenseCategory,
        notes: String,
        date: Long,
        paymentMethod: String,
        receiptPath: String?,
    ) {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            val conversionResult = calculateExpenseUseCase.calculateHomeAmount(
                amountLocal = amountLocal,
                currencyCode = currencyCode,
                homeCurrencyCode = uiState.value.userCurrencyCode
            )

            val expense = Expense(
                id = id,
                tripId = currentTrip.id,
                title = title.ifBlank { "Expense" },
                amountLocal = amountLocal,
                currencyCode = currencyCode.uppercase().trim(),
                amountHome = conversionResult.amountHome,
                isUnconverted = conversionResult.isUnconverted,
                category = category,
                date = date,
                notes = notes,
                paymentMethod = paymentMethod,
                receiptPath = receiptPath,
            )

            expenseRepository.updateExpense(expense)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }

    fun calculateLiveConversion(
        amountLocal: Double,
        currencyCode: String,
        callback: (Double, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = calculateExpenseUseCase.calculateHomeAmount(
                amountLocal = amountLocal,
                currencyCode = currencyCode,
                homeCurrencyCode = uiState.value.userCurrencyCode
            )
            callback(result.amountHome, result.isUnconverted)
        }
    }

    // Workspace Attachments Actions
    fun addFileAttachment(
        context: Context,
        uri: Uri,
        type: AttachmentType,
        customTitle: String,
        category: String = "General"
    ) {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            val fileInfo = FileStorageHelper.saveUriToInternalStorage(context, uri)
            if (fileInfo != null) {
                val titleToUse = customTitle.ifBlank { fileInfo.title }
                val nextOrder = (uiState.value.workspaceAttachments.maxOfOrNull { it.displayOrder } ?: -1) + 1
                val attachment = TripAttachment(
                    tripId = currentTrip.id,
                    type = type,
                    title = titleToUse,
                    filePath = fileInfo.filePath,
                    fileSize = fileInfo.fileSize,
                    mimeType = fileInfo.mimeType,
                    createdAt = System.currentTimeMillis(),
                    displayOrder = nextOrder,
                    category = category
                )
                tripRepository.addAttachment(attachment)
            }
        }
    }

    fun addNoteAttachment(title: String, text: String, category: String = "Notes") {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            val nextOrder = (uiState.value.workspaceAttachments.maxOfOrNull { it.displayOrder } ?: -1) + 1
            val attachment = TripAttachment(
                tripId = currentTrip.id,
                type = AttachmentType.NOTE,
                title = title.ifBlank { "Trip Note" },
                content = text,
                createdAt = System.currentTimeMillis(),
                displayOrder = nextOrder,
                category = category
            )
            tripRepository.addAttachment(attachment)
        }
    }

    fun updateNoteAttachment(attachmentId: Long, title: String, text: String) {
        val existing = uiState.value.workspaceAttachments.find { it.id == attachmentId } ?: return
        viewModelScope.launch {
            val updated = existing.copy(
                title = title.ifBlank { "Trip Note" },
                content = text
            )
            tripRepository.addAttachment(updated)
        }
    }

    fun moveAttachment(fromIndex: Int, toIndex: Int) {
        val currentAttachments = uiState.value.workspaceAttachments.toMutableList()
        if (fromIndex !in currentAttachments.indices || toIndex !in currentAttachments.indices) return
        val movedItem = currentAttachments.removeAt(fromIndex)
        currentAttachments.add(toIndex, movedItem)

        val reordered = currentAttachments.mapIndexed { index, item ->
            item.copy(displayOrder = index)
        }
        viewModelScope.launch {
            tripRepository.updateAttachments(reordered)
        }
    }

    fun deleteAttachment(attachmentId: Long) {
        viewModelScope.launch {
            tripRepository.deleteAttachment(attachmentId)
        }
    }

    fun openAddDialog(cca3: String? = null) {
        _uiState.value = _uiState.value.copy(
            isAddDialogOpen = true,
            selectedCca3 = cca3 ?: _uiState.value.selectedCca3
        )
    }

    fun closeAddDialog() {
        _uiState.value = _uiState.value.copy(isAddDialogOpen = false)
    }

    fun onCountryChanged(cca3: String) {
        _uiState.value = _uiState.value.copy(selectedCca3 = cca3)
    }

    fun onStartDateChanged(date: String) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }

    fun onEndDateChanged(date: String) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }

    fun onBudgetChanged(budget: String) {
        _uiState.value = _uiState.value.copy(budgetUsd = budget)
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun updateTripDetails(trip: Trip, startDate: String, endDate: String, budgetUsd: Double) {
        viewModelScope.launch {
            val updatedTrip = trip.copy(
                startDate = startDate,
                endDate = endDate,
                budgetUsd = budgetUsd
            )
            tripRepository.addTrip(updatedTrip)
            if (_uiState.value.activeWorkspaceTrip?.id == trip.id) {
                _uiState.value = _uiState.value.copy(activeWorkspaceTrip = updatedTrip)
            }
        }
    }

    fun saveTrip() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val country = _uiState.value.availableCountries.find { it.cca3.equals(_uiState.value.selectedCca3, ignoreCase = true) }
            val trip = Trip(
                destinationCca3 = _uiState.value.selectedCca3,
                countryName = country?.commonName ?: _uiState.value.selectedCca3,
                flagEmoji = country?.flagEmoji ?: "",
                flagUrl = country?.flagUrl ?: "",
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate,
                budgetUsd = _uiState.value.budgetUsd.toDoubleOrNull() ?: 2000.0,
                notes = _uiState.value.notes,
                status = "Planned"
            )
            tripRepository.addTrip(trip)
            _uiState.value = _uiState.value.copy(isSaving = false, isAddDialogOpen = false)
        }
    }

    fun deleteTrip(id: Int) {
        viewModelScope.launch {
            if (_uiState.value.activeWorkspaceTrip?.id == id) {
                closeTripWorkspace()
            }
            tripRepository.deleteTrip(id)
        }
    }
}
