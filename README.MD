# WebSocket Subscription Management with Error Handling

A Spring Boot application that demonstrates robust WebSocket subscription management with comprehensive error handling capabilities.

## Features

- **WebSocket Communication**: Real-time bidirectional communication using STOMP protocol
- **Subscription Management**: Track and manage active WebSocket subscriptions
- **Error Handling**: Comprehensive error handling at multiple levels:
  - Connection errors
  - Message processing errors
  - Subscription management errors
- **User-Specific Messaging**: Send messages to specific users through queues
- **SockJS Fallback**: Support for browsers that don't support WebSocket

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- A modern web browser
- Basic understanding of WebSocket and STOMP protocol

## Project Structure

```
src/main/java/com/example/websocketsubscription/
├── WebSocketSubscriptionApplication.java    # Main application class
├── config/
│   └── WebSocketConfig.java                 # WebSocket configuration
├── controller/
│   └── WebSocketController.java             # Message handling controller
├── service/
│   └── SubscriptionService.java             # Subscription management service
└── listener/
    └── WebSocketEventListener.java          # WebSocket event handling
```

## Running the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd websocket-subscription
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Testing the Application

### Using a WebSocket Client

1. **Connect to WebSocket**:
   - WebSocket endpoint: `ws://localhost:8080/ws`
   - SockJS endpoint: `http://localhost:8080/ws`

2. **Subscribe to Topics**:
   - Subscribe to general topics: `/topic/*`
   - Subscribe to user-specific queues: `/user/queue/*`

3. **Send Messages**:
   - Send messages to: `/app/send`
   - Message format:
     ```json
     {
       "message": "Your message here"
     }
     ```

### Example Client Code

Here's a simple HTML/JavaScript example to test the WebSocket functionality:

```html
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Test Client</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.5.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>
    <div>
        <button onclick="connect()">Connect</button>
        <button onclick="disconnect()">Disconnect</button>
    </div>
    <div>
        <input type="text" id="message" placeholder="Enter message">
        <button onclick="sendMessage()">Send</button>
    </div>
    <div id="messages"></div>

    <script>
        let stompClient = null;

        function connect() {
            const socket = new SockJS('http://localhost:8080/ws');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);
                
                // Subscribe to user-specific queue
                stompClient.subscribe('/user/queue/messages', function (message) {
                    showMessage(JSON.parse(message.body).response);
                });

                // Subscribe to error queue
                stompClient.subscribe('/user/queue/errors', function (message) {
                    showError(JSON.parse(message.body).error);
                });
            });
        }

        function disconnect() {
            if (stompClient !== null) {
                stompClient.disconnect();
            }
            console.log("Disconnected");
        }

        function sendMessage() {
            const message = document.getElementById('message').value;
            stompClient.send("/app/send", {}, JSON.stringify({'message': message}));
        }

        function showMessage(message) {
            const messagesDiv = document.getElementById('messages');
            messagesDiv.innerHTML += '<p>Response: ' + message + '</p>';
        }

        function showError(error) {
            const messagesDiv = document.getElementById('messages');
            messagesDiv.innerHTML += '<p style="color: red">Error: ' + error + '</p>';
        }
    </script>
</body>
</html>
```

### Testing Scenarios

1. **Basic Connection**:
   - Connect to WebSocket
   - Verify connection success in server logs
   - Send a message and verify response

2. **Error Handling**:
   - Disconnect network to test connection errors
   - Send invalid messages to test message processing errors
   - Monitor error messages in both client and server logs

3. **Subscription Management**:
   - Subscribe to multiple topics
   - Verify subscription tracking in server logs
   - Unsubscribe and verify cleanup

## Monitoring and Logging

The application provides detailed logging for:
- Connection events
- Subscription changes
- Message processing
- Error conditions

Check the application logs for real-time monitoring of WebSocket activities.

## Troubleshooting

1. **Connection Issues**:
   - Verify the server is running
   - Check network connectivity
   - Ensure correct WebSocket endpoint URL

2. **Message Delivery Issues**:
   - Verify subscription to correct topics/queues
   - Check message format
   - Monitor server logs for errors

3. **Subscription Issues**:
   - Verify subscription paths
   - Check user session management
   - Monitor subscription service logs

## Contributing

Feel free to submit issues and enhancement requests!
