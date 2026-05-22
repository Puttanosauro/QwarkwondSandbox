# 🪐 Qwarkdown Collaborative Editor

A real-time, WebAssembly-powered collaborative text editor tailored for physics, mathematics, and scientific papers.
> **Note:** This project is currently in active development (Phase 1: Local Sandbox).

## 🛠 Project Vision
Standard word processors (like Google Docs) struggle with the complexities of STEM notation. **Qwarkdown (.qd)** is a custom markup language designed to bridge this gap. By offloading heavy computational rendering to the client via **WebAssembly (WASM)**, we provide a latency-free editing experience while keeping our backend as a lightweight router for synchronization.

## 🏗 Architecture
* **Frontend:** Kotlin Multiplatform (WASM) handling local rendering with a two-tier system: real-time markdown preview and debounced math formula rendering.
* **Backend:** Kotlin (Ktor) managing WebSocket connections and CRDT validation.
* **Sync:** CRDT-based synchronization (e.g., Yjs) for mathematically safe, simultaneous multi-user editing.
* **Persistence:** A hybrid approach using Redis for active session caching, an append-only log for version history, and SQL for snapshots.

## 🛠 Development Roadmap
We are currently in **Phase 1: The Local Sandbox**.
### Completed
* Initiated port of `quarkdown-core` to Kotlin Multiplatform (KMP).
* Refactored `ast` package to remove JVM-specific dependencies (`java.io`, thread pools).

### In Progress / Next Steps
//todo
## 🧩 Technical Note:
WASM and the JVM don't exactly love one another.
Because of this, many features available in the original QD (qwardwon) code simply aren't supported in the WebAssembly port

To maintain a full, functional port, I’ve had to make some compromises
  * **"Lobotomized" Classes:** Many classes in the WASM version are stripped-down versions of their JVM counterparts and might behave slightly differently than what standard QD does.
  *  **Interface Abstraction:** This is achieved using Kotlin interfaces in `commonMain`.

What this approach achieves is a mostly seamless experience while editing and ensuring at the same time that the server HTML/PDF renders are accurate
### Feature Parity & Limitations
| Feature                    | Browser/WASM Behavior | Server-Side Implementation | developer comment                                                                                               |
|:---------------------------|:----------------------|:---------------------------|:----------------------------------------------------------------------------------------------------------------|
| **Multi-thread Rendering** | Stripped/Disabled     | still missing              | in a web page this is mostly useless and the difference is barely notable                                       |
| **CSL Citation Rendering** | Placeholder/Stubbed   | still missing              | the numbering and citation wont fully work as intended, tho the placeholder are designed to be atleast readable |

> **Note:** *We use abstraction via `commonMain` interfaces to ensure that features which cannot run in the browser can still be fully implemented on the server-side compiler for accurate final PDF/HTML rendering*