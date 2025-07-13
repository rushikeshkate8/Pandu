package com.rushikesh.pandu.ui.screens.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.rushikesh.pandu.ui.states.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.rushikesh.pandu.R

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    remember { mutableIntStateOf(0) }
    val placeholderPrompt = ""
    val placeholderResult = stringResource(R.string.results_placeholder)

    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by homeViewModel.uiState.collectAsState()
    var generateContentResponse: GenerateContentResponse? = null
    var textColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        bottomBar = { MessageInput(homeViewModel) },
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                result = ""
            }
            if (uiState is UiState.Error) {
                textColor = MaterialTheme.colorScheme.error
                result = (uiState as UiState.Error).errorMessage
            } else if (uiState is UiState.Success) {
                textColor = MaterialTheme.colorScheme.onSurface
                LaunchedEffect(uiState) {
                    GlobalScope.launch(Dispatchers.IO) {
                        result = ""
                        (uiState as UiState.Success).output.collectLatest {
                            result += it.text ?: ""
                        }
                    }
                }
            }

            val scrollState = rememberScrollState()
            Text(
                text = result,
                textAlign = TextAlign.Start,
                color = textColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(viewModel: HomeViewModel) {
    var prompt by rememberSaveable { mutableStateOf("") }
    //val isExpanded = text.isNotEmpty() // Example: Expands when text is entered
    var isFocused by remember { mutableStateOf(false) }
    // Adjust height and shape based on the state
    val height = if (isFocused) 120.dp else 70.dp
    val shape = RoundedCornerShape(if (isFocused) 20.dp else 50.dp)
    Row(verticalAlignment = Alignment.Bottom) {
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Message", color = Color(0xFF909090)) },
            singleLine = false, // Allows multi-line when expanded
            modifier = Modifier
                .fillMaxWidth()
                .height(height)// Adjust height dynamically
                .padding(8.dp)
                .onFocusChanged { isFocused = it.isFocused },
            shape = shape, // Rounded rectangle or capsule based on state
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF242424), // Background when focused
                unfocusedContainerColor = Color(0xFF242424), // Background when unfocused
                focusedIndicatorColor = Color.Transparent, // No underline
                unfocusedIndicatorColor = Color.Transparent, // No underline
                disabledIndicatorColor = Color.Transparent, // No underline
                cursorColor = Color.White
            ),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.sendPrompt(prompt)
                }) {
                    Icon(
                        painterResource(R.drawable.baseline_arrow_circle_left_24),
                        contentDescription = "Send Message",
                        modifier = Modifier.size(48.dp), tint = Color.White
                    )
                }
            },
        )
    }
}
