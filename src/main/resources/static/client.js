// --- Global State ---
const SERVER_URL = 'http://localhost:8080';
const WS_ENDPOINT = '/ws';
const GLOBAL_TOPIC = '/topic/global-notifications';
const PRIVATE_QUEUE = '/user/queue/private-messages'; // The dedicated user queue
let stompClient = null;

const logDiv = document.getElementById('log');
const loginButton = document.getElementById('loginButton');
const registerButton = document.getElementById('registerButton');
const connectButton = document.getElementById('connectButton');
const disconnectButton = document.getElementById('disconnectButton');
const broadcastButton = document.getElementById('broadcastButton');
const privateButton = document.getElementById('privateButton');

// Helper function to update the UI log
function logMessage(message, type = 'info') {
    const p = document.createElement('div');
    p.innerHTML = `[${new Date().toLocaleTimeString()}] ${message}`;

    if (type === 'notification') {
        p.className = 'notification'; // Global Message Style
    } else if (type === 'private') {
        p.className = 'private-notification'; // Private Message Style
    } else if (type === 'error') {
        p.style.color = 'red';
    }
    logDiv.prepend(p);
}

// --- UI State Management ---
function updateConnectionState(isConnected) {
    connectButton.disabled = isConnected;
    disconnectButton.disabled = !isConnected;
    broadcastButton.disabled = !isConnected;
    privateButton.disabled = !isConnected;
}

// --- 1. Authentication Functions ---

// 1a. Login Function (HTTP POST - Basic Auth or Session)
async function handleLogin() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    logMessage(`Attempting login for user: **${username}**...`);
    loginButton.disabled = true;

    // Use Fetch API for login (credentials will be sent via Basic Auth if enabled on server)
    try {
        const authString = btoa(`${username}:${password}`); // Base64 encoding for Basic Auth
        const response = await fetch(`${SERVER_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // This header isn't strictly needed for the /login endpoint
                // but included here if /login is disabled and we just rely on the session
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok || response.status === 200) {
            logMessage('**Login Successful!** You can now connect and send messages.', 'info');
            connectButton.disabled = false;
        } else {
            const errorText = await response.text();
            logMessage(`Login Failed: ${response.status} - ${errorText}`, 'error');
            loginButton.disabled = false;
        }
    } catch (error) {
        logMessage(`Network Error: ${error.message}`, 'error');
        loginButton.disabled = false;
    }
}

// 1b. Registration Function (HTTP POST) - (Logic remains the same)
async function handleRegister() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;

    if (!username || !password) {
        logMessage("Username and Password are required.", 'error');
        return;
    }

    logMessage(`Attempting registration for user: **${username}**...`);
    registerButton.disabled = true;

    try {
        const response = await fetch(`${SERVER_URL}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const responseText = await response.text();

        if (response.status === 201) {
            logMessage(`**Registration Successful!** You can now log in.`, 'info');
        } else {
            logMessage(`Registration Failed: ${response.status} - ${responseText}`, 'error');
        }
    } catch (error) {
        logMessage(`Network Error during registration: ${error.message}`, 'error');
    } finally {
        registerButton.disabled = false;
    }
}


// --- 2. WebSocket Functions ---

function connectAndSubscribe() {
    if (stompClient && stompClient.connected) {
        logMessage('Already connected.', 'info');
        return;
    }

    const socket = new SockJS(`${SERVER_URL}${WS_ENDPOINT}`);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        logMessage('WebSocket **Connected** via STOMP.', 'info');
        updateConnectionState(true);

        // 1. Subscribe to the Global Broadcast Topic
        stompClient.subscribe(GLOBAL_TOPIC, function (notification) {
            const body = JSON.parse(notification.body);
            logMessage(`**[GLOBAL] ${body.title}** from ${body.sender}: ${body.content}`, 'notification');
        });

        // 2. ✨ Subscribe to the Private User Queue (The key change)
        stompClient.subscribe(PRIVATE_QUEUE, function (notification) {
            const body = JSON.parse(notification.body);
            logMessage(`**[PRIVATE] ${body.title}** from ${body.sender}: ${body.content}`, 'private');
        });

        logMessage(`Subscribed to **Global** and **Private** queues.`, 'info');

    }, function(error) {
        logMessage(`STOMP Connection Error: ${error}`, 'error');
        updateConnectionState(false);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        logMessage('Disconnected from WebSocket.', 'info');
        updateConnectionState(false);
        loginButton.disabled = false;
        registerButton.disabled = false;
    }
}

// --- 3. Message Sending Functions ---

// 3a. Handle Global Broadcast Send (Raw Text)
async function handleBroadcast() {
    const content = document.getElementById('broadcast-content').value;
    if (!content) return;

    logMessage(`Attempting global broadcast...`, 'info');

    // Authentication is handled by the Authorization header (Basic Auth)
    // or the browser automatically including the JSESSIONID cookie.
    try {
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;

        const response = await fetch(`${SERVER_URL}/api/admin/broadcast`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
                // Include Basic Auth header for stateless API access
                'Authorization': 'Basic ' + btoa(`${username}:${password}`)
            },
            body: content
        });

        if (response.ok) {
            logMessage('Global message accepted by server.', 'info');
            document.getElementById('broadcast-content').value = '';
        } else {
            const errorText = await response.text();
            logMessage(`Broadcast Failed: ${response.status} - ${errorText}`, 'error');
        }
    } catch (error) {
        logMessage(`Network Error during broadcast: ${error.message}`, 'error');
    }
}

// 3b. ✨ Handle Private Message Send (Structured JSON)
async function handlePrivateSend() {
    const targetUsername = document.getElementById('private-target').value;
    const title = document.getElementById('private-title').value;
    const content = document.getElementById('private-content').value;

    if (!targetUsername || !title || !content) {
        logMessage('All private message fields are required.', 'error');
        return;
    }

    logMessage(`Attempting private send to **${targetUsername}**...`, 'info');

    const messagePayload = {
        targetUsername: targetUsername,
        title: title,
        content: content
    };

    try {
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;

        const response = await fetch(`${SERVER_URL}/api/admin/send-private`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Include Basic Auth header for stateless API access
                'Authorization': 'Basic ' + btoa(`${username}:${password}`)
            },
            body: JSON.stringify(messagePayload)
        });

        if (response.ok) {
            logMessage(`Private message sent to **${targetUsername}**.`, 'info');
            document.getElementById('private-content').value = '';
            document.getElementById('private-title').value = '';
        } else {
            const errorText = await response.text();
            logMessage(`Private Send Failed: ${response.status} - ${errorText}`, 'error');
        }

    } catch (error) {
        logMessage(`Network Error during private send: ${error.message}`, 'error');
    }
}