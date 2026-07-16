import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LedgerLiteServer {
    
    private static final int PORT = 8080;
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, String> faceData = new HashMap<>();
    private static final Map<String, Double> loyaltyPoints = new HashMap<>();
    private static final Map<String, List<Map<String, Object>>> invoices = new HashMap<>();
    private static final Map<String, List<Map<String, Object>>> tasks = new HashMap<>();
    private static final Map<String, List<Map<String, Object>>> messages = new HashMap<>();
    
    static class User {
        String password;
        boolean premium;
        String businessName;
        String businessLocation;
        String businessType;
        String subscriptionExpiry;
        int level;
        String grade;
        String tier;
        int businessAge;
        double totalSales;
        int totalOrders;
        String joinDate;
        String lastLogin;
        String theme;
        String currency;
        String phone;
        String email;
        String website;
        
        User(String password) {
            this.password = password;
            this.premium = false;
            this.level = 1;
            this.grade = "A";
            this.tier = "Bronze";
            this.businessAge = 0;
            this.totalSales = 0;
            this.totalOrders = 0;
            this.joinDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            this.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.theme = "dark";
            this.currency = "ZAR";
            this.phone = "";
            this.email = "";
            this.website = "";
            this.subscriptionExpiry = LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
    
    public static void main(String[] args) throws IOException {
        // Add default admin user
        User admin = new User("admin123");
        admin.premium = true;
        admin.businessName = "LedgerLite Corp";
        admin.businessLocation = "Cape Town, South Africa";
        admin.businessType = "Technology & Accounting";
        admin.level = 99;
        admin.grade = "A+++";
        admin.tier = "Platinum";
        admin.businessAge = 10;
        admin.totalSales = 1500000;
        admin.totalOrders = 5000;
        admin.currency = "ZAR";
        admin.phone = "+27 82 123 4567";
        admin.email = "admin@ledgerlite.com";
        admin.website = "https://ledgerlite.com";
        admin.subscriptionExpiry = LocalDateTime.now().plusYears(10).format(DateTimeFormatter.ISO_LOCAL_DATE);
        users.put("admin", admin);
        loyaltyPoints.put("admin", 9999.0);
        
        User manager = new User("manager123");
        manager.businessName = "Manager Solutions";
        manager.businessLocation = "Johannesburg, South Africa";
        manager.businessType = "Business Consulting";
        manager.level = 50;
        manager.grade = "A+";
        manager.tier = "Gold";
        manager.businessAge = 5;
        manager.totalSales = 500000;
        manager.totalOrders = 1500;
        manager.currency = "ZAR";
        users.put("manager", manager);
        loyaltyPoints.put("manager", 2500.0);
        
        addSampleData();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Register ALL handlers
        server.createContext("/", new RootHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/signup", new SignupHandler());
        server.createContext("/api/face/enroll", new FaceEnrollHandler());
        server.createContext("/api/face/recognize", new FaceRecognizeHandler());
        server.createContext("/api/update-business", new UpdateBusinessHandler());
        server.createContext("/api/get-user", new GetUserHandler());
        server.createContext("/api/invoice", new InvoiceHandler());
        server.createContext("/api/task", new TaskHandler());
        server.createContext("/api/message", new MessageHandler());
        server.createContext("/api/loyalty", new LoyaltyHandler());
        server.createContext("/api/analytics", new AnalyticsHandler());
        server.createContext("/api/export", new ExportHandler());
        server.createContext("/api/theme", new ThemeHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("========================================");
        System.out.println("🚀 LedgerLite Premium A+++ Server");
        System.out.println("========================================");
        System.out.println("📍 http://localhost:" + PORT);
        System.out.println("========================================");
        System.out.println("🔑 Login: admin / admin123 (Premium)");
        System.out.println("📝 Sign up: Create new business account");
        System.out.println("========================================");
        System.out.println("💎 Features: Analytics, Invoices, Tasks, Messages");
        System.out.println("⭐ Level 99 • Grade A+++ • Platinum Tier");
        System.out.println("========================================");
        System.out.println("Press Ctrl+C to stop");
        System.out.println("========================================");
    }
    
    private static void addSampleData() {
        // Sample invoices for admin
        List<Map<String, Object>> adminInvoices = new ArrayList<>();
        Map<String, Object> inv1 = new HashMap<>();
        inv1.put("id", UUID.randomUUID().toString());
        inv1.put("client", "TechCorp Solutions");
        inv1.put("amount", 15000.0);
        inv1.put("description", "Software Development Services");
        inv1.put("date", "2026-07-01");
        inv1.put("status", "Paid");
        inv1.put("dueDate", "2026-07-15");
        adminInvoices.add(inv1);
        
        Map<String, Object> inv2 = new HashMap<>();
        inv2.put("id", UUID.randomUUID().toString());
        inv2.put("client", "Green Energy Ltd");
        inv2.put("amount", 8500.0);
        inv2.put("description", "Consulting Services");
        inv2.put("date", "2026-07-03");
        inv2.put("status", "Pending");
        inv2.put("dueDate", "2026-07-31");
        adminInvoices.add(inv2);
        
        invoices.put("admin", adminInvoices);
        
        // Sample tasks for admin
        List<Map<String, Object>> adminTasks = new ArrayList<>();
        Map<String, Object> task1 = new HashMap<>();
        task1.put("id", UUID.randomUUID().toString());
        task1.put("title", "Q3 Financial Review");
        task1.put("description", "Prepare quarterly financial report");
        task1.put("priority", "High");
        task1.put("status", "In Progress");
        task1.put("created", LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        task1.put("dueDate", LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE));
        adminTasks.add(task1);
        
        tasks.put("admin", adminTasks);
        
        // Sample messages for admin
        List<Map<String, Object>> adminMessages = new ArrayList<>();
        Map<String, Object> msg1 = new HashMap<>();
        msg1.put("id", UUID.randomUUID().toString());
        msg1.put("from", "manager");
        msg1.put("to", "admin");
        msg1.put("subject", "Q3 Financial Report");
        msg1.put("content", "Please review the Q3 financial report.");
        msg1.put("date", LocalDateTime.now().minusHours(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        msg1.put("read", false);
        adminMessages.add(msg1);
        
        messages.put("admin", adminMessages);
    }
    
    // ============================================================
    // ROOT HANDLER - Serves index.html
    // ============================================================
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            System.out.println("📄 Serving: " + path);
            
            if (path.equals("/") || path.equals("/index.html")) {
                try {
                    // Read the HTML file
                    String html = new String(Files.readAllBytes(Paths.get("index.html")), StandardCharsets.UTF_8);
                    
                    // Set CORS headers
                    exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    
                    byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                    System.out.println("✅ index.html served successfully");
                    return;
                } catch (IOException e) {
                    System.out.println("❌ index.html not found!");
                    String html = getFallbackHTML();
                    byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }
            } else if (path.startsWith("/images/")) {
                try {
                    String filePath = path.substring(1);
                    byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                    String contentType = getContentType(filePath);
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, fileBytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(fileBytes);
                    os.close();
                    return;
                } catch (IOException e) {
                    System.out.println("❌ Image not found: " + path);
                    String response = "Image not found";
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(404, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }
            }
            
            String response = "Not Found";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(404, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
    
    // ============================================================
    // LOGIN HANDLER
    // ============================================================
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("🔐 Login request received");
            
            // Handle CORS preflight
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                String response = "Method Not Allowed";
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(405, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
                return;
            }
            
            String body = readRequestBody(exchange);
            System.out.println("📝 Login body: " + body);
            
            Map<String, String> params = parseFormData(body);
            String username = params.get("username");
            String password = params.get("password");
            
            System.out.println("👤 Username: " + username);
            
            // Check hardcoded admin first
            if ("admin".equals(username) && "admin123".equals(password)) {
                User user = users.get("admin");
                user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                
                String response = buildUserResponse("admin", user);
                System.out.println("✅ Admin login successful");
                sendJsonResponse(exchange, 200, response);
                return;
            }
            
            // Check manager
            if ("manager".equals(username) && "manager123".equals(password)) {
                User user = users.get("manager");
                user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                
                String response = buildUserResponse("manager", user);
                System.out.println("✅ Manager login successful");
                sendJsonResponse(exchange, 200, response);
                return;
            }
            
            // Check registered users
            if (users.containsKey(username)) {
                User user = users.get(username);
                if (user.password.equals(password)) {
                    user.lastLogin = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    
                    String response = buildUserResponse(username, user);
                    System.out.println("✅ Login successful: " + username);
                    sendJsonResponse(exchange, 200, response);
                    return;
                }
            }
            
            System.out.println("❌ Login failed for: " + username);
            String response = "{\"success\":false,\"message\":\"Invalid credentials\"}";
            sendJsonResponse(exchange, 401, response);
        }
    }
    
    private static String buildUserResponse(String username, User user) {
        double loyaltyPointsVal = loyaltyPoints.getOrDefault(username, 0.0);
        return "{\"success\":true,\"premium\":" + user.premium + 
            ",\"username\":\"" + username + 
            "\",\"businessName\":\"" + (user.businessName != null ? user.businessName : "") + 
            "\",\"businessLocation\":\"" + (user.businessLocation != null ? user.businessLocation : "") + 
            "\",\"businessType\":\"" + (user.businessType != null ? user.businessType : "") + 
            "\",\"level\":" + user.level + 
            ",\"grade\":\"" + user.grade + 
            "\",\"tier\":\"" + user.tier + 
            "\",\"businessAge\":" + user.businessAge + 
            ",\"totalSales\":" + user.totalSales + 
            ",\"totalOrders\":" + user.totalOrders + 
            ",\"joinDate\":\"" + user.joinDate + 
            "\",\"lastLogin\":\"" + user.lastLogin + 
            "\",\"currency\":\"" + user.currency + 
            "\",\"theme\":\"" + user.theme + 
            "\",\"phone\":\"" + (user.phone != null ? user.phone : "") + 
            "\",\"email\":\"" + (user.email != null ? user.email : "") + 
            "\",\"website\":\"" + (user.website != null ? user.website : "") + 
            "\",\"expiry\":\"" + user.subscriptionExpiry + 
            "\",\"loyaltyPoints\":" + loyaltyPointsVal + "}";
    }
    
    // ============================================================
    // SIGNUP HANDLER
    // ============================================================
    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📝 Signup request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method Not Allowed\"}");
                return;
            }
            
            String body = readRequestBody(exchange);
            System.out.println("📝 Signup body: " + body);
            
            Map<String, String> params = parseFormData(body);
            String username = params.get("username");
            String password = params.get("password");
            String businessName = params.get("businessName");
            String businessLocation = params.get("businessLocation");
            String businessType = params.get("businessType");
            String currency = params.get("currency");
            
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Please fill in all required fields\"}");
                return;
            }
            
            if (users.containsKey(username) || "admin".equals(username) || "manager".equals(username)) {
                sendJsonResponse(exchange, 409, "{\"success\":false,\"message\":\"Username already exists\"}");
                return;
            }
            
            User newUser = new User(password);
            newUser.businessName = businessName != null ? businessName : "New Business";
            newUser.businessLocation = businessLocation != null ? businessLocation : "South Africa";
            newUser.businessType = businessType != null ? businessType : "Business";
            newUser.currency = currency != null ? currency : "ZAR";
            newUser.level = 1;
            newUser.grade = "A";
            newUser.tier = "Bronze";
            
            users.put(username, newUser);
            loyaltyPoints.put(username, 100.0);
            
            String response = "{\"success\":true,\"message\":\"Business account created\",\"premium\":false,\"username\":\"" + username + 
                "\",\"businessName\":\"" + newUser.businessName + 
                "\",\"businessLocation\":\"" + newUser.businessLocation + 
                "\",\"businessType\":\"" + newUser.businessType + 
                "\",\"level\":" + newUser.level + 
                ",\"grade\":\"" + newUser.grade + 
                "\",\"tier\":\"" + newUser.tier + 
                "\",\"currency\":\"" + newUser.currency + 
                "\",\"loyaltyPoints\":100.0}";
            
            System.out.println("✅ Signup successful: " + username);
            sendJsonResponse(exchange, 200, response);
        }
    }
    
    // ============================================================
    // FACE ENROLL HANDLER
    // ============================================================
    static class FaceEnrollHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📸 Face enroll request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method Not Allowed\"}");
                return;
            }
            
            String body = readRequestBody(exchange);
            Map<String, String> params = parseFormData(body);
            String username = params.get("username");
            String faceDataStr = params.get("faceData");
            
            if (username == null || faceDataStr == null) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing face data\"}");
                return;
            }
            
            faceData.put(username, faceDataStr);
            loyaltyPoints.put(username, loyaltyPoints.getOrDefault(username, 0.0) + 20);
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Face enrolled\",\"points\":20}");
        }
    }
    
    // ============================================================
    // FACE RECOGNIZE HANDLER
    // ============================================================
    static class FaceRecognizeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📸 Face recognize request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method Not Allowed\"}");
                return;
            }
            
            String body = readRequestBody(exchange);
            Map<String, String> params = parseFormData(body);
            String faceDataStr = params.get("faceData");
            
            if (faceDataStr == null) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing face data\"}");
                return;
            }
            
            String matchedUser = null;
            for (Map.Entry<String, String> entry : faceData.entrySet()) {
                if (entry.getValue().equals(faceDataStr)) {
                    matchedUser = entry.getKey();
                    break;
                }
            }
            
            if (matchedUser != null && users.containsKey(matchedUser)) {
                User user = users.get(matchedUser);
                String response = "{\"success\":true,\"username\":\"" + matchedUser + 
                    "\",\"premium\":" + user.premium + 
                    ",\"level\":" + user.level + 
                    ",\"grade\":\"" + user.grade + 
                    "\",\"tier\":\"" + user.tier + "\"}";
                sendJsonResponse(exchange, 200, response);
            } else {
                sendJsonResponse(exchange, 200, "{\"success\":false,\"message\":\"Face not recognized\"}");
            }
        }
    }
    
    // ============================================================
    // GET USER HANDLER
    // ============================================================
    static class GetUserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("👤 Get user request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            String query = exchange.getRequestURI().getQuery();
            String username = null;
            if (query != null) {
                String[] params = query.split("=");
                if (params.length == 2 && params[0].equals("username")) {
                    username = params[1];
                }
            }
            
            if (username == null || !users.containsKey(username)) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"User not found\"}");
                return;
            }
            
            User user = users.get(username);
            double loyaltyPointsVal = loyaltyPoints.getOrDefault(username, 0.0);
            
            String response = "{\"success\":true,\"username\":\"" + username + 
                "\",\"businessName\":\"" + (user.businessName != null ? user.businessName : "") + 
                "\",\"businessLocation\":\"" + (user.businessLocation != null ? user.businessLocation : "") + 
                "\",\"businessType\":\"" + (user.businessType != null ? user.businessType : "") + 
                "\",\"level\":" + user.level + 
                ",\"grade\":\"" + user.grade + 
                "\",\"tier\":\"" + user.tier + 
                "\",\"premium\":" + user.premium + 
                ",\"expiry\":\"" + user.subscriptionExpiry + 
                "\",\"businessAge\":" + user.businessAge + 
                ",\"totalSales\":" + user.totalSales + 
                ",\"totalOrders\":" + user.totalOrders + 
                ",\"joinDate\":\"" + user.joinDate + 
                "\",\"lastLogin\":\"" + user.lastLogin + 
                "\",\"currency\":\"" + user.currency + 
                "\",\"theme\":\"" + user.theme + 
                "\",\"phone\":\"" + (user.phone != null ? user.phone : "") + 
                "\",\"email\":\"" + (user.email != null ? user.email : "") + 
                "\",\"website\":\"" + (user.website != null ? user.website : "") + 
                "\",\"loyaltyPoints\":" + loyaltyPointsVal + "}";
            sendJsonResponse(exchange, 200, response);
        }
    }
    
    // ============================================================
    // INVOICE HANDLER
    // ============================================================
    static class InvoiceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📋 Invoice request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormData(body);
                
                String username = params.get("username");
                String client = params.get("client");
                String amount = params.get("amount");
                String description = params.get("description");
                
                if (username != null && client != null && amount != null) {
                    Map<String, Object> invoice = new HashMap<>();
                    invoice.put("id", UUID.randomUUID().toString());
                    invoice.put("client", client);
                    invoice.put("amount", Double.parseDouble(amount));
                    invoice.put("description", description != null ? description : "Invoice");
                    invoice.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    invoice.put("status", "Pending");
                    invoice.put("dueDate", LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE));
                    
                    invoices.computeIfAbsent(username, k -> new ArrayList<>()).add(invoice);
                    loyaltyPoints.put(username, loyaltyPoints.getOrDefault(username, 0.0) + 5);
                    
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Invoice created\",\"points\":5}");
                    return;
                }
            } else if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null && invoices.containsKey(username)) {
                    List<Map<String, Object>> userInvoices = invoices.get(username);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < userInvoices.size(); i++) {
                        Map<String, Object> inv = userInvoices.get(i);
                        json.append("{");
                        json.append("\"id\":\"").append(inv.get("id")).append("\",");
                        json.append("\"client\":\"").append(inv.get("client")).append("\",");
                        json.append("\"amount\":").append(inv.get("amount")).append(",");
                        json.append("\"description\":\"").append(inv.get("description")).append("\",");
                        json.append("\"date\":\"").append(inv.get("date")).append("\",");
                        json.append("\"status\":\"").append(inv.get("status")).append("\",");
                        json.append("\"dueDate\":\"").append(inv.get("dueDate")).append("\"");
                        json.append("}");
                        if (i < userInvoices.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"invoices\":" + json.toString() + "}");
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"invoices\":[]}");
        }
    }
    
    // ============================================================
    // TASK HANDLER
    // ============================================================
    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("✅ Task request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormData(body);
                
                String username = params.get("username");
                String title = params.get("title");
                String description = params.get("description");
                String priority = params.get("priority");
                
                if (username != null && title != null) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("id", UUID.randomUUID().toString());
                    task.put("title", title);
                    task.put("description", description != null ? description : "");
                    task.put("priority", priority != null ? priority : "Medium");
                    task.put("status", "Pending");
                    task.put("created", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    task.put("dueDate", LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
                    
                    tasks.computeIfAbsent(username, k -> new ArrayList<>()).add(task);
                    loyaltyPoints.put(username, loyaltyPoints.getOrDefault(username, 0.0) + 3);
                    
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Task created\",\"points\":3}");
                    return;
                }
            } else if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null && tasks.containsKey(username)) {
                    List<Map<String, Object>> userTasks = tasks.get(username);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < userTasks.size(); i++) {
                        Map<String, Object> task = userTasks.get(i);
                        json.append("{");
                        json.append("\"id\":\"").append(task.get("id")).append("\",");
                        json.append("\"title\":\"").append(task.get("title")).append("\",");
                        json.append("\"description\":\"").append(task.get("description")).append("\",");
                        json.append("\"priority\":\"").append(task.get("priority")).append("\",");
                        json.append("\"status\":\"").append(task.get("status")).append("\",");
                        json.append("\"created\":\"").append(task.get("created")).append("\",");
                        json.append("\"dueDate\":\"").append(task.get("dueDate")).append("\"");
                        json.append("}");
                        if (i < userTasks.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"tasks\":" + json.toString() + "}");
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"tasks\":[]}");
        }
    }
    
    // ============================================================
    // MESSAGE HANDLER
    // ============================================================
    static class MessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("💬 Message request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormData(body);
                
                String username = params.get("username");
                String to = params.get("to");
                String subject = params.get("subject");
                String content = params.get("content");
                
                if (username != null && to != null && content != null) {
                    Map<String, Object> message = new HashMap<>();
                    message.put("id", UUID.randomUUID().toString());
                    message.put("from", username);
                    message.put("to", to);
                    message.put("subject", subject != null ? subject : "New Message");
                    message.put("content", content);
                    message.put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    message.put("read", false);
                    
                    messages.computeIfAbsent(to, k -> new ArrayList<>()).add(message);
                    loyaltyPoints.put(username, loyaltyPoints.getOrDefault(username, 0.0) + 2);
                    
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Message sent\",\"points\":2}");
                    return;
                }
            } else if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null && messages.containsKey(username)) {
                    List<Map<String, Object>> userMessages = messages.get(username);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < userMessages.size(); i++) {
                        Map<String, Object> msg = userMessages.get(i);
                        json.append("{");
                        json.append("\"id\":\"").append(msg.get("id")).append("\",");
                        json.append("\"from\":\"").append(msg.get("from")).append("\",");
                        json.append("\"to\":\"").append(msg.get("to")).append("\",");
                        json.append("\"subject\":\"").append(msg.get("subject")).append("\",");
                        json.append("\"content\":\"").append(msg.get("content").toString().replace("\"", "\\\"")).append("\",");
                        json.append("\"date\":\"").append(msg.get("date")).append("\",");
                        json.append("\"read\":").append(msg.get("read"));
                        json.append("}");
                        if (i < userMessages.size() - 1) json.append(",");
                    }
                    json.append("]");
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"messages\":" + json.toString() + "}");
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"messages\":[]}");
        }
    }
    
    // ============================================================
    // LOYALTY HANDLER
    // ============================================================
    static class LoyaltyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("🎯 Loyalty request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null) {
                    double points = loyaltyPoints.getOrDefault(username, 0.0);
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"points\":" + points + "}");
                    return;
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormData(body);
                
                String username = params.get("username");
                String action = params.get("action");
                String points = params.get("points");
                
                if (username != null && action != null) {
                    double currentPoints = loyaltyPoints.getOrDefault(username, 0.0);
                    double addPoints = points != null ? Double.parseDouble(points) : 10.0;
                    
                    if ("add".equals(action)) {
                        loyaltyPoints.put(username, currentPoints + addPoints);
                    } else if ("redeem".equals(action)) {
                        if (currentPoints >= addPoints) {
                            loyaltyPoints.put(username, currentPoints - addPoints);
                        }
                    }
                    
                    sendJsonResponse(exchange, 200, 
                        "{\"success\":true,\"points\":" + loyaltyPoints.get(username) + "}");
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"points\":0}");
        }
    }
    
    // ============================================================
    // ANALYTICS HANDLER
    // ============================================================
    static class AnalyticsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📊 Analytics request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null && users.containsKey(username)) {
                    User user = users.get(username);
                    double loyaltyPointsVal = loyaltyPoints.getOrDefault(username, 0.0);
                    
                    String response = "{\"success\":true," +
                        "\"totalRevenue\":" + user.totalSales + "," +
                        "\"totalOrders\":" + user.totalOrders + "," +
                        "\"loyaltyPoints\":" + loyaltyPointsVal + "," +
                        "\"level\":" + user.level + "," +
                        "\"grade\":\"" + user.grade + "\"," +
                        "\"tier\":\"" + user.tier + "\"}";
                    sendJsonResponse(exchange, 200, response);
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":true,\"totalRevenue\":0,\"totalOrders\":0}");
        }
    }
    
    // ============================================================
    // EXPORT HANDLER
    // ============================================================
    static class ExportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("📄 Export request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String username = null;
                if (query != null) {
                    String[] params = query.split("=");
                    if (params.length == 2 && params[0].equals("username")) {
                        username = params[1];
                    }
                }
                
                if (username != null && users.containsKey(username)) {
                    User user = users.get(username);
                    double loyaltyPointsVal = loyaltyPoints.getOrDefault(username, 0.0);
                    
                    String csv = "Business Report for " + username + "\n" +
                        "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n\n" +
                        "Business: " + user.businessName + "\n" +
                        "Location: " + user.businessLocation + "\n" +
                        "Level: " + user.level + "\n" +
                        "Grade: " + user.grade + "\n" +
                        "Tier: " + user.tier + "\n" +
                        "Total Sales: " + user.totalSales + "\n" +
                        "Total Orders: " + user.totalOrders + "\n" +
                        "Loyalty Points: " + loyaltyPointsVal + "\n";
                    
                    String response = "{\"success\":true,\"csv\":\"" + csv.replace("\"", "\\\"").replace("\n", "\\n") + "\"}";
                    sendJsonResponse(exchange, 200, response);
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":false,\"message\":\"Export failed\"}");
        }
    }
    
    // ============================================================
    // THEME HANDLER
    // ============================================================
    static class ThemeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("🎨 Theme request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = readRequestBody(exchange);
                Map<String, String> params = parseFormData(body);
                
                String username = params.get("username");
                String theme = params.get("theme");
                
                if (username != null && users.containsKey(username) && theme != null) {
                    users.get(username).theme = theme;
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"theme\":\"" + theme + "\"}");
                    return;
                }
            }
            
            sendJsonResponse(exchange, 200, "{\"success\":false,\"message\":\"Theme update failed\"}");
        }
    }
    
    // ============================================================
    // UPDATE BUSINESS HANDLER
    // ============================================================
    static class UpdateBusinessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("🏢 Update business request received");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method Not Allowed\"}");
                return;
            }
            
            String body = readRequestBody(exchange);
            Map<String, String> params = parseFormData(body);
            
            String username = params.get("username");
            String businessName = params.get("businessName");
            String businessLocation = params.get("businessLocation");
            String businessType = params.get("businessType");
            String currency = params.get("currency");
            
            if (!users.containsKey(username)) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"User not found\"}");
                return;
            }
            
            User user = users.get(username);
            if (businessName != null) user.businessName = businessName;
            if (businessLocation != null) user.businessLocation = businessLocation;
            if (businessType != null) user.businessType = businessType;
            if (currency != null) user.currency = currency;
            
            // Level up logic
            if (user.premium) {
                user.level = Math.min(99, user.level + 1);
                if (user.level >= 90) { user.grade = "A+++"; user.tier = "Platinum"; }
                else if (user.level >= 75) { user.grade = "A++"; user.tier = "Gold"; }
                else if (user.level >= 50) { user.grade = "A+"; user.tier = "Silver"; }
                else if (user.level >= 25) { user.grade = "A"; user.tier = "Bronze"; }
                else { user.grade = "B"; user.tier = "Basic"; }
            }
            
            user.businessAge++;
            
            String response = "{\"success\":true,\"message\":\"Business updated\",\"level\":" + user.level + 
                ",\"grade\":\"" + user.grade + 
                "\",\"tier\":\"" + user.tier + 
                "\",\"businessAge\":" + user.businessAge + "}";
            sendJsonResponse(exchange, 200, response);
        }
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
    
    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        if (formData == null || formData.isEmpty()) return params;
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
    
    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".svg")) return "image/svg+xml";
        if (filePath.endsWith(".webp")) return "image/webp";
        return "text/plain";
    }
    
    private static String getFallbackHTML() {
        return "<!DOCTYPE html><html><head><title>LedgerLite</title></head><body><h1>LedgerLite</h1><p>Please create index.html file in the same folder.</p></body></html>";
    }
}
