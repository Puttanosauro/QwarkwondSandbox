# 🪐 Quarkdown Collaborative Editor

![Status](https://img.shields.io/badge/Status-Pre--Alpha_(Phase_1)-red) [![License: AGPLv3](https://img.shields.io/badge/License-AGPLv3-blue)](LICENSE)

A real-time, Kotlin Multiplatform-powered collaborative text editor tailored for physics, mathematics, and scientific papers.

> [!WARNING]
> **Note:** This project is currently in an early stage of development (Phase 1: Local Sandbox). 
>
>*More info in a later paragraph*

## 🛠 Project Vision
We are a "team" of just 2 STEM students that just wanted a system to write notes and share them reliably with friends or teacher, 
but it is tragic how hard it is to find a system that reliably achieves it while also being usable by a normal human being.

What this project aims to achieve is a home-made fix:
it is a conversion layer written in Kotlin Multiplatform for [Quarkdown (.qd) language](https://github.com/iamgio/quarkdown)
that aims to provide a Google Docs-style collaborative environment with usable UI elements and buttons. **just like every platform like this should be!**

It works by offloading heavy computational rendering to the client via **WebAssembly (WASM)** which allows for a lightweight and self-hostable backend.

We wanted something where you don't have to compile a massive document just to see if your equation looks right. 
You get a live preview alongside the raw `.qd` file, so you can tweak the code or use the UI buttons without breaking your flow

### Why Quarkdown?
Standard Markdown breaks down when handling complex scientific papers. 
Quarkdown solves this natively, which is why we chose it as our foundation. 

For more information about Quarkdown and `.qd` we recommend checking out [the original repo](https://github.com/iamgio/quarkdown) 


## Getting Started *(Please Don't)*
**TL;DR: Do not try to run this yet. It is completely broken, and we know it.**

Right now, this repository serves strictly as a version control backup for our active development. 
We are right in the middle of tearing out JVM dependencies and rewriting core logic for WASM. 
If you try to clone and build this right now, you will only be met with something close to 400 compiler errors, 
missing files and other problems I myself am yet to discover.

Once we actually have a functional local sandbox we will update this section with real build instructions. 
Until then, feel free to look at the code, but don't expect it to compile!

## 🏗 Architecture
* **The Core:** Leverages the [Quarkdown](https://github.com/iamgio/quarkdown) ecosystem, ported to Kotlin Multiplatform (KMP/WASM) to enable high-performance client-side rendering.
* **Frontend:** A dual-mode interface offering real-time visual rendering alongside raw `.qd` source control.
* **Backend:** Kotlin (Ktor) acting as a lightweight sync-router, document validator, and state manager.
* **Sync & Persistence:** CRDT-based synchronization for real-time collaboration, paired with a hybrid storage model (Redis for session caching, append-only logs for history, and SQL for final snapshots).

## 🛠 Development Roadmap
We are currently working on **Phase 1 out of 7** of the development.

Because of this everything might be changed going forwards
### Completed
* Initiated port of `quarkdown-core` to Kotlin Multiplatform (KMP).
* removed the JVM-specific dependencies from the KMP version:
  * Refactored `ast` package to remove JVM-specific dependencies (`java.io`, thread pools).
  * Refactored `bibliography` package to remove JVM-specific dependencies (`java.io`).

### In Progress / Next Steps
To finish phase 1 we need to:
* finish the port of of `quarkdown-core` to Kotlin Multiplatform (KMP), missing:
  * [context](app/shared/src/commonMain/kotlin/org/example/project/qdcore/context)
  * [document](app/shared/src/commonMain/kotlin/org/example/project/qdcore/document)
  * [flavor](app/shared/src/commonMain/kotlin/org/example/project/qdcore/flavor)
  * [function](app/shared/src/commonMain/kotlin/org/example/project/qdcore/function)
  * [graph](app/shared/src/commonMain/kotlin/org/example/project/qdcore/graph)
  * [lexer](app/shared/src/commonMain/kotlin/org/example/project/qdcore/lexer)
  * [localization](app/shared/src/commonMain/kotlin/org/example/project/qdcore/localization)
  * [log](app/shared/src/commonMain/kotlin/org/example/project/qdcore/log)
  * [media](app/shared/src/commonMain/kotlin/org/example/project/qdcore/media)
  * [misc](app/shared/src/commonMain/kotlin/org/example/project/qdcore/misc)
  * [parser](app/shared/src/commonMain/kotlin/org/example/project/qdcore/parser)
  * [permissions](app/shared/src/commonMain/kotlin/org/example/project/qdcore/permissions)
  * [pipeline](app/shared/src/commonMain/kotlin/org/example/project/qdcore/pipeline)
  * [property](app/shared/src/commonMain/kotlin/org/example/project/qdcore/property)
  * [rendering](app/shared/src/commonMain/kotlin/org/example/project/qdcore/rendering)
  * [template](app/shared/src/commonMain/kotlin/org/example/project/qdcore/template)
  * [util](app/shared/src/commonMain/kotlin/org/example/project/qdcore/util)
  * [visitor](app/shared/src/commonMain/kotlin/org/example/project/qdcore/visitor)
  
## 🧩 Technical Note:
WASM and the JVM don't exactly love one another.
Because of this, many features available in the original QD (quardwon) code simply aren't supported in the WebAssembly port

To maintain a full, functional port, I’ve had to make some compromises
  * **"Lobotomized" Classes:** Many classes in the WASM version are stripped-down versions of their JVM counterparts and might behave slightly differently than what standard QD does.
  *  **Interface Abstraction:** This is achieved using Kotlin interfaces in `commonMain`.

What this approach achieves is a mostly seamless experience while editing and ensuring at the same time that the server HTML/PDF renders are accurate
### Feature Parity & Limitations
| Feature                    | Browser/WASM Behavior | Server-Side Implementation | developer comment                                                                                                                                         |
|:---------------------------|:----------------------|:---------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Multi-thread Rendering** | Stripped/Disabled     | still missing              | in a web page this is mostly useless and the difference is barely notable                                                                                 |
| **CSL Citation Rendering** | Placeholder/Stubbed   | working                    | the numbering and citation wont fully work as intended, tho the placeholder are designed to be atleast readable                                           |
| **IO Utilities**           | mostly working        | working                    | the structure has been rewritten in Kotlin to avoid JVM-hell, it should do for 99.9% of client use cases                                                  |
| **Escape Html**            | Adapted               | working                    | the funcion relies on `org.apache.commons.text.StringEscapeUtils` because of this a full rewrite is near-impossible, but this should work well-ish enough |
| **URL resolve tools**      | Adapted               | working                    | the funcion relies on `java.net` because of this a clientside restructure was needed                                                                      |

> **Note:** *We use abstraction via `commonMain` interfaces to ensure that features which cannot run in the browser can still be fully implemented on the server-side compiler for accurate final PDF/HTML rendering*

# Was AI used for this project?
to answer the question directly: **yes, generative AI was used during the development process**.

As a team of just two people building a complex system that realistically demands a much larger team, using these tools was an unfortunate necessity just to keep up with the boilerplate and ship the project. 

There is an inherent sadness in how predominant AI has become in creative and engineering spaces, often filling gaps that should belong to human hands. 

But the saddest part of all is how difficult it has become to distinguish between genuine work and "AI SLOP", 
because of this we want to emphasize that the AI was strictly treated as a tool for repetitive tasks. 
Every architectural decision, the system design, the "what goes where," and the logic bridging WASM and the JVM was entirely conceived, supervised, driven or straight up written by us.