# Cake Haven

Cake Haven is a client-server ordering system. Users connect with a username, browse 3 categories (Cakes, Cupcakes, Pastries), add items to a cart and see the quantities and the total. The server accepts any number of clients (one thread per client).

## Files

The whole project is `pom.xml` and `src/`. All code is in one folder, `src/com/mycompany/cakehaven/` (package `com.mycompany.cakehaven`), and the pictures are in its `images/` folder.

| Student | Part | Files |
|---|---|---|
| 1 | Server | `CakeHavenServer` |
| 2 | Client connection | `ConnectFrame`, `ServerConnection` |
| 3 | Products & categories | `ProductPanel`, `ProductCard`, `Product`, `Category`, `ProductRepository`, `SampleProductData`, `ProductImages`, `RoundedButton`, `Theme`, `ProductSelectionListener`, `ProductRepositoryListener` |
| 4 | Shopping cart | `Cart`, `CartItem`, `CartPanel`, `CartFrame` |
| 5 | Integration | `StoreFrame` (joins everything; Cart and My Orders buttons) |

## Messages (one line of text each, port 5050)

| Client sends | Server replies |
|---|---|
| `CONNECT:<username>` | `CONNECTED` or `ERROR:<reason>` |

Reasons: `Expected CONNECT:username`, `Username is empty`, `Username already taken`.

## Run in NetBeans

1. **File → Open Project…** and pick this folder (the one with `pom.xml`).
2. Start the server: right-click `CakeHavenServer.java` → **Run File**. The Output shows `Cake Haven Server is running on port 5050...`.
3. Start a client: click **Run Project** (green ▶). It opens the Connect screen. Do this once per user, each with a different username.
4. Clients on other laptops: type the server laptop's IP in **Server IP**. All laptops must be on the same network (a phone hotspot works best), and the server laptop must allow Java through its firewall.

## Phase 1 status

| Requirement | Status |
|---|---|
| 1. Connect (register username, show "connected") | ✅ |
| 2. Browse 3 categories × 4 items (image, name, price, stock, quantity, Add) | ✅ |
| 3. Cart button: items, quantity of each, total | ✅ (can't add more than the stock) |
| Server keeps unlimited open connections | ✅ |
| Complete interface for all functions | ✅ Order button (cart) and My Orders window with Cancel Order; their server part is Phase 2 |
