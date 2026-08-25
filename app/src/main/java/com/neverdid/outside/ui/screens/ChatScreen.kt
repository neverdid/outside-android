package com.neverdid.outside.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.neverdid.outside.model.ChatMessage
import com.neverdid.outside.model.Conversation
import com.neverdid.outside.ui.components.OutsideAvatar
import com.neverdid.outside.ui.theme.Forest
import com.neverdid.outside.ui.theme.Lime

@Composable
fun ChatScreen(
    conversation: Conversation,
    messages: List<ChatMessage>,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
) {
    var draft by remember(conversation.id) { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
    ) {
        ChatHeader(conversation = conversation, onBack = onBack)
        if (conversation.activity != null) {
            ActivityContext(conversation.activity)
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (messages.isEmpty()) {
                item {
                    Text(
                        "Say hello — a shared hobby already gives you something to talk about.",
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(messages.size, key = { messages[it].id }) { index ->
                MessageBubble(messages[index])
            }
        }
        MessageComposer(
            draft = draft,
            onDraftChange = { draft = it },
            onSend = {
                val body = draft.trim()
                if (body.isNotEmpty()) {
                    onSendMessage(body)
                    draft = ""
                }
            },
        )
    }
}

@Composable
private fun ChatHeader(conversation: Conversation, onBack: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            OutsideAvatar(
                initials = conversation.initials,
                modifier = Modifier.size(42.dp),
                background = if (conversation.isGroup) Lime else MaterialTheme.colorScheme.surfaceVariant,
                foreground = Forest,
            )
            Spacer(Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(conversation.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (conversation.isGroup) "${conversation.unread + 6} members · active" else "Usually replies today",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = "Conversation options")
            }
        }
    }
}

@Composable
private fun ActivityContext(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Lime.copy(alpha = 0.55f))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("UPCOMING", color = Forest, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.size(9.dp))
        Text(label, color = Forest, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.82f),
            horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start,
        ) {
            if (!message.isMine) {
                Text(
                    text = message.sender,
                    modifier = Modifier.padding(start = 12.dp, bottom = 3.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (message.isMine) 18.dp else 5.dp,
                            bottomEnd = if (message.isMine) 5.dp else 18.dp,
                        ),
                    )
                    .background(if (message.isMine) Forest else MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    message.body,
                    color = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                text = message.time,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Add attachment")
            }
            TextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
                maxLines = 4,
                shape = RoundedCornerShape(22.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
            IconButton(
                onClick = onSend,
                enabled = draft.isNotBlank(),
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (draft.isNotBlank()) Forest else MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (draft.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
