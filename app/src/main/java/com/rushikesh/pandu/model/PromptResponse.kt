package com.rushikesh.pandu.model

import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

data class PromptResponse(val text:  Flow<GenerateContentResponse>)