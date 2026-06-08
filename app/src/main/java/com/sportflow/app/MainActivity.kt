package com.sportflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sportflow.app.ui.theme.SportFlowTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val supabase = createSupabaseClient(
    supabaseUrl = "https://gjlbllzruxlvypcngtda.supabase.co",
    supabaseKey = "sb_publishable_R4Y8NPzM8CQsYwdLoWU8TQ_psbdoM2U"
) {
    install(Postgrest)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoList()
                }
            }
        }
    }
}

@Composable
fun TodoList() {
    var items by remember { mutableStateOf<List<TodoItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        val todoItems = withContext(Dispatchers.IO) {
            supabase.from("todos").select().decodeList<TodoItem>()
        }
        items = todoItems
    }

    LazyColumn {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            Text(
                text = item.name,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}