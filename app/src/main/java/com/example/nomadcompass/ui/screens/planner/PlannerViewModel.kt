package com.example.nomadcompass.ui.screens.planner

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomadcompass.domain.model.AttachmentType
import com.example.nomadcompass.domain.model.Country
import com.example.nomadcompass.domain.model.Trip
import com.example.nomadcompass.domain.model.TripAttachment
import com.example.nomadcompass.domain.repository.TripRepository
import com.example.nomadcompass.domain.usecase.GetAllCountriesUseCase
import com.example.nomadcompass.util.FileStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.nomadcompass.domain.usecase.GetProfileUseCase

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
    val workspaceAttachments: List<TripAttachment> = emptyList(),
    val userCurrencyCode: String = "USD",
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val tripRepository: TripRepository,
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

    private val _attachmentsFlow = _activeTripId.flatMapLatest { tripId ->
        if (tripId != null) {
            tripRepository.getAttachmentsForTrip(tripId)
        } else {
            flowOf(emptyList())
        }
    }

    val uiState: StateFlow<PlannerUiState> = combine(
        _uiState,
        tripRepository.getAllTrips(),
        getAllCountriesUseCase(),
        _attachmentsFlow,
        getProfileUseCase()
    ) { state, trips, countries, attachments, profile ->
        val sortedCountries = countries.sortedWith(
            compareByDescending<Country> { it.isFavorite }.thenBy { it.commonName }
        )
        val currency = profile?.baseCurrencyCode?.ifBlank { "USD" } ?: "USD"
        state.copy(
            trips = trips,
            availableCountries = sortedCountries,
            workspaceAttachments = attachments,
            userCurrencyCode = currency
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlannerUiState()
    )

    fun openTripWorkspace(trip: Trip) {
        _activeTripId.value = trip.id
        _uiState.value = _uiState.value.copy(activeWorkspaceTrip = trip)
    }

    fun closeTripWorkspace() {
        _activeTripId.value = null
        _uiState.value = _uiState.value.copy(activeWorkspaceTrip = null)
    }

    fun addFileAttachment(context: Context, uri: Uri, type: AttachmentType, customTitle: String) {
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
                    displayOrder = nextOrder
                )
                tripRepository.addAttachment(attachment)
            }
        }
    }

    fun addNoteAttachment(title: String, text: String) {
        val currentTrip = uiState.value.activeWorkspaceTrip ?: return
        viewModelScope.launch {
            val nextOrder = (uiState.value.workspaceAttachments.maxOfOrNull { it.displayOrder } ?: -1) + 1
            val attachment = TripAttachment(
                tripId = currentTrip.id,
                type = AttachmentType.NOTE,
                title = title.ifBlank { "Trip Note" },
                content = text,
                createdAt = System.currentTimeMillis(),
                displayOrder = nextOrder
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

    fun saveTrip() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val country = _uiState.value.availableCountries.find { it.cca3 == _uiState.value.selectedCca3 }
            val trip = Trip(
                destinationCca3 = _uiState.value.selectedCca3,
                countryName = country?.commonName ?: _uiState.value.selectedCca3,
                flagEmoji = country?.flagEmoji ?: "✈️",
                flagUrl = country?.flagUrl ?: "",
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate,
                budgetUsd = _uiState.value.budgetUsd.toDoubleOrNull() ?: 2000.0,
                notes = _uiState.value.notes,
                status = "Upcoming"
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

