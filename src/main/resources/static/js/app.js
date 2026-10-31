// API Endpoints
const API_SEND_MESSAGE = '/msg/send';
const API_GET_STATS = '/api/stats';
const API_GET_MESSAGES = '/api/messages';
const API_CLEAR_MESSAGES = '/api/messages/clear';
const STATS_REFRESH_INTERVAL = 30000; // 30 seconds
const MESSAGES_REFRESH_INTERVAL = 5000; // 5 seconds

/**
 * Send a message to Kafka
 */
function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();

    if (!message) {
        showErrorResponse('请输入消息内容');
        return;
    }

    const responseDiv = document.getElementById('response');
    const responseText = document.getElementById('responseText');

    const params = new URLSearchParams();
    params.append('message', message);

    fetch(API_SEND_MESSAGE + '?' + params.toString())
        .then(response => response.text())
        .then(data => {
            responseDiv.classList.remove('error');
            responseDiv.classList.add('show');
            responseText.textContent = '✅ ' + data;
            // Refresh stats and messages after sending
            setTimeout(() => {
                refreshStats();
                loadMessages();
            }, 1000);
        })
        .catch(error => {
            showErrorResponse('❌ 错误: ' + error.message);
        });
}

/**
 * Refresh Kafka statistics
 */
function refreshStats() {
    fetch(API_GET_STATS)
        .then(response => response.json())
        .then(data => {
            updateStatsDisplay(data);
        })
        .catch(error => {
            console.error('Error fetching stats:', error);
            displayStatsError();
        });
}

/**
 * Update statistics display with fetched data
 */
function updateStatsDisplay(data) {
    if (data.error) {
        displayStatsError();
        return;
    }

    const totalMessages = data.totalMessages || 0;
    const consumerOffset = data.consumerOffset || 0;
    const lag = data.lag || 0;
    const percentage = totalMessages > 0 ? Math.round((consumerOffset / totalMessages) * 100) : 0;

    // Update DOM elements
    document.getElementById('totalMessages').innerHTML = totalMessages;
    document.getElementById('consumerOffset').innerHTML = consumerOffset;
    document.getElementById('lag').innerHTML = lag;
    document.getElementById('progress').innerHTML = percentage + '<span class="stat-unit">%</span>';
}

/**
 * Display error state for statistics
 */
function displayStatsError() {
    const errorText = '连接失败';
    document.getElementById('totalMessages').innerHTML = errorText;
    document.getElementById('consumerOffset').innerHTML = errorText;
    document.getElementById('lag').innerHTML = errorText;
    document.getElementById('progress').innerHTML = errorText;
}

/**
 * Show error response message
 */
function showErrorResponse(message) {
    const responseDiv = document.getElementById('response');
    const responseText = document.getElementById('responseText');
    responseDiv.classList.add('error');
    responseDiv.classList.add('show');
    responseText.textContent = message;
}

/**
 * Clear the message input
 */
function clearInput() {
    document.getElementById('messageInput').value = '';
    document.getElementById('messageInput').focus();
}

/**
 * Load and display consumed messages
 */
function loadMessages() {
    const container = document.getElementById('messagesContainer');
    container.innerHTML = '<div class="loading-text">加载消息中...</div>';

    fetch(API_GET_MESSAGES + '?limit=20')
        .then(response => response.json())
        .then(data => {
            if (!data.messages || data.messages.length === 0) {
                container.innerHTML = '<div class="empty-text">暂无消费的消息</div>';
                return;
            }

            const html = data.messages.map(msg => `
                <div class="message-item">
                    <div class="message-content">${escapeHtml(msg.content)}</div>
                    <div class="message-time">${msg.timestamp}</div>
                </div>
            `).join('');

            container.innerHTML = html;
        })
        .catch(error => {
            console.error('Error loading messages:', error);
            container.innerHTML = '<div class="empty-text">加载失败</div>';
        });
}

/**
 * Clear consumed messages
 */
function clearConsumedMessages() {
    if (!confirm('确定要清空所有消费的消息吗？')) {
        return;
    }

    fetch(API_CLEAR_MESSAGES)
        .then(response => response.json())
        .then(data => {
            loadMessages();
        })
        .catch(error => {
            console.error('Error clearing messages:', error);
            alert('清空失败');
        });
}

/**
 * Escape HTML special characters
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/**
 * Initialize application on page load
 */
function initializeApp() {
    // Load stats on page load
    refreshStats();
    loadMessages();

    // Auto-refresh stats every 30 seconds
    setInterval(refreshStats, STATS_REFRESH_INTERVAL);

    // Auto-refresh messages every 5 seconds
    setInterval(loadMessages, MESSAGES_REFRESH_INTERVAL);

    console.log('Kafka Demo App initialized');
}

/**
 * Document ready handler
 */
document.addEventListener('DOMContentLoaded', initializeApp);
