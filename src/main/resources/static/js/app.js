// API Endpoints
const API_SEND_MESSAGE = '/msg/send';
const API_GET_STATS = '/api/stats';
const STATS_REFRESH_INTERVAL = 30000; // 30 seconds

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
            // Refresh stats after sending message
            setTimeout(refreshStats, 1000);
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
 * Initialize application on page load
 */
function initializeApp() {
    // Load stats on page load
    refreshStats();

    // Auto-refresh stats every 30 seconds
    setInterval(refreshStats, STATS_REFRESH_INTERVAL);

    console.log('Kafka Demo App initialized');
}

/**
 * Document ready handler
 */
document.addEventListener('DOMContentLoaded', initializeApp);
