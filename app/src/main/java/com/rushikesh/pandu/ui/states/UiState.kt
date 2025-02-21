package com.rushikesh.pandu.ui.states

import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    data class Loading(val msg: String = "Loading") : UiState

    /**
     * Text has been generated
     */
    data class Success(val output:  Flow<GenerateContentResponse>) : UiState

    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState
}