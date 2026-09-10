# WASTA?

### Find the right service, not the right person.

**WASTA?** is an AI-powered mobile platform designed to solve a simple but common problem in Algeria:

> **“Tu connais quelqu’un qui fait ça ?”**

Finding a plumber, electrician, mechanic, appliance technician, AC repair specialist, phone technician, cleaner, locksmith, or another local professional often depends on personal contacts, Facebook groups, WhatsApp conversations, or random recommendations.

WASTA? aims to replace this fragmented process with a structured, intelligent, and trusted digital experience.

---

## 🚀 What is WASTA?

WASTA? allows users to simply **describe their problem in natural language** instead of searching through hundreds of service categories.

For example:

> “Mon chauffe-eau fuit depuis ce matin.”

or

> “Ma machine à laver fait un bruit bizarre et ne vide plus l'eau.”

The platform uses AI to understand the request, identify the appropriate service category and specialty, determine urgency, and help find the most suitable professionals nearby.

The goal is simple:

**You don't need to know who to call. WASTA? helps you find who can help.**

---

## 🎯 The Problem

Local services in Algeria are often highly fragmented.

Customers frequently rely on:

* Friends and family recommendations
* Facebook groups
* WhatsApp
* Phone calls
* Informal contacts
* Random online searches
* Unverified service providers

This creates several problems:

* Difficult professional discovery
* Lack of reliable information
* No structured reputation system
* Unclear pricing
* Poor availability visibility
* Difficult appointment management
* Limited trust between customers and professionals
* No centralized service history

At the same time, many skilled professionals have no professional digital presence and depend heavily on word-of-mouth.

---

## 💡 The Solution

WASTA? creates a digital infrastructure connecting customers with local professionals.

### Customer

**Describe → Match → Book → Track → Review**

### Professional

**Register → Verify → Receive requests → Accept → Complete → Build reputation**

### Platform

**AI → Matching → Trust → Job management → Digital records**

---

## 🧠 AI-Powered Service Matching

Instead of forcing users to understand technical service categories, WASTA? lets them communicate naturally.

### Example

**User input:**

> “سلام، سخان الماء تاعي راه يقطر من لتحت.”

The AI can transform the request into structured information such as:

```json
{
  "category": "home_services",
  "service": "water_heater_repair",
  "specialty": "water_heater_technician",
  "urgency": "medium",
  "location_required": true
}
```

The AI is used for **classification, understanding, and triage**, not as a replacement for the professional's final diagnosis.

---

## 🔎 Smart Matching

Professionals are matched according to relevant factors such as:

* Service specialty
* Location
* Service area
* Availability
* Rating
* Completed jobs
* Experience
* Response time
* Reliability
* Verification status
* Price range
* Recent activity

The objective is not simply to show the closest professional.

It is to find the **most suitable professional for the specific request**.

---

## 🔄 Service Lifecycle

```text
Customer
   ↓
Describe the problem
   ↓
AI understanding & classification
   ↓
Smart professional matching
   ↓
Professional accepts request
   ↓
Appointment
   ↓
Service performed
   ↓
Before / after proof
   ↓
Digital invoice
   ↓
Payment
   ↓
Customer review
   ↓
Professional reputation
```

---

## 👤 Customer Features

Customers can:

* Create an account
* Describe problems using natural language
* Use Arabic, French, English, or mixed Algerian language
* Select their location
* Receive intelligent service recommendations
* Discover suitable professionals
* View professional profiles
* Request a service
* Schedule appointments
* Track job progress
* Receive notifications
* Upload or view job-related proof
* Receive invoices
* Manage payments
* Review professionals
* Access service history

---

## 🛠️ Professional Features

Professionals can:

* Create a professional profile
* Select their specialties
* Define their service area
* Manage availability
* Receive service requests
* Accept or reject jobs
* Manage appointments
* Update job status
* Upload before/after proof
* Maintain job history
* Track completed services
* Build their professional reputation

---

## ⭐ Trust & Reputation

Trust is one of the core components of WASTA?.

Professional profiles can progressively build reputation through real activity:

* Completed jobs
* Verified reviews
* Customer satisfaction
* Response time
* Reliability
* Cancellation rate
* Completion rate
* Verification status
* Service history

Reviews should be connected to completed jobs whenever possible to reduce fake or manipulated ratings.

---

## 📍 Location

Location is used to improve professional matching.

The platform can consider:

* Customer location
* Professional location
* Professional service area
* Distance
* Availability

Privacy is important.

The application should only request precise location information when it is actually necessary and should avoid unnecessarily exposing a customer's exact location.

---

## 💳 Payments & Invoices

WASTA? is designed to support a complete service transaction:

```text
Request
   ↓
Appointment
   ↓
Service
   ↓
Invoice
   ↓
Payment
   ↓
Transaction history
```

Payment and financial information must always be validated on the server side.

The client application should never be trusted to determine:

* Final payment status
* Transaction amount
* Refund status
* Professional balance
* Payment confirmation

---

## 🔐 Security

Security is a fundamental part of the platform.

Important security requirements include:

* Secure authentication
* Server-side authorization
* Role-based access control
* Protection against IDOR/BOLA
* Input validation
* API rate limiting
* Secure file uploads
* Secure session/token handling
* HTTPS communication
* Secure storage of sensitive mobile data
* Protection against fake reviews
* Payment verification
* Audit logs
* Location privacy
* Secret management
* Protection against AI prompt injection
* Protection against AI cost abuse

The AI output is treated as **untrusted data** and must never directly control sensitive operations such as authentication, payments, verification, or authorization.

---

## 🧪 Testing

The project should be tested across multiple layers.

### Unit Tests

Core business logic and utilities.

### Integration Tests

API, database, authentication, matching, appointments, and payment flows.

### End-to-End Tests

Important real-world scenarios:

```text
Customer creates request
        ↓
AI classifies request
        ↓
Professionals are matched
        ↓
Professional accepts
        ↓
Appointment created
        ↓
Job completed
        ↓
Review submitted
```

### Security Tests

Important scenarios include:

* Customer A cannot access Customer B's jobs
* Professional A cannot modify Professional B's jobs
* Two professionals cannot accept the same request
* Customer cannot manipulate payment status
* Reviews cannot be submitted before job completion
* Unauthorized users cannot escalate their role
* Malicious files are rejected
* Large malicious requests are rejected
* Expensive AI endpoints are rate limited

---

## 🇩🇿 Algeria-First Design

WASTA? is designed with the Algerian market in mind.

The platform should support:

* Arabic
* French
* English
* Algerian dialect input
* RTL interfaces
* Mobile-first UX
* Low-bandwidth environments
* Local service categories
* Local professional workflows
* Simple and accessible user experiences

The goal is not to copy an existing international marketplace.

The goal is to build something adapted to **how Algerians actually find and use local services**.

---

## 🏗️ Product Philosophy

WASTA? is **not simply a directory of professionals**.

It is not:

> “Here is a list of plumbers.”

It is:

> **“Tell us what is wrong, and we help you find the right person.”**

The long-term value comes from combining:

**AI + Local Discovery + Matching + Trust + Reputation + Service History**

---

## 📱 Example Use Case

### Problem

A user has a leaking water heater.

Instead of searching:

```text
plombier Blida
```

or asking:

```text
“Tu connais un bon plombier ?”
```

the user writes:

```text
“Mon chauffe-eau fuit et j'ai besoin de quelqu'un rapidement.”
```

WASTA? understands the request and can identify:

```text
Service:
Water Heater Repair

Specialty:
Water Heater Technician

Urgency:
High

Location:
Blida
```

The platform then presents suitable professionals based on matching criteria.

The customer selects a professional, schedules the intervention, follows the job, receives the service, and leaves a review.

---

## 🗺️ Roadmap

### Phase 1 — MVP

* Authentication
* Customer accounts
* Professional accounts
* Service requests
* AI classification
* Location
* Professional matching
* Professional profiles
* Appointment management
* Reviews

### Phase 2 — Trust

* Professional verification
* Completed-job reputation
* Job proof
* Service history
* Reliability metrics
* Dispute management

### Phase 3 — Transactions

* Digital invoices
* Payment integration
* Transaction history
* Refund handling

### Phase 4 — Growth

* More service categories
* More cities
* Advanced matching
* Professional analytics
* Admin dashboard
* Notifications
* Advanced AI assistance

### Phase 5 — Scale

```text
Blida
  ↓
Algiers
  ↓
Boumerdes / Tipaza
  ↓
Oran
  ↓
Constantine
  ↓
Nationwide
```

---

## 🎯 Initial Target Market

The platform can initially focus on a limited number of high-demand categories:

* Plumbing
* Electricity
* Water heater repair
* Air conditioning
* Appliance repair
* Phone repair
* Computer repair
* Locksmith services
* Cleaning
* Moving services

Starting with a limited geographic area allows the marketplace and reputation system to become useful before expanding nationwide.

---

## 💼 Business Model

Potential monetization models include:

### Commission

A percentage of completed transactions.

### Lead Fee

Professionals pay for qualified service requests.

### Professional Subscription

Advanced features for active professionals.

### Hybrid Model

A combination of transaction fees and professional subscriptions.

The initial priority should be **product-market fit and trust**, not maximizing monetization from day one.

---

## 🧩 Project Architecture

> **Important:** The exact technologies used by this project should be documented based on the actual source code. Do not assume or invent technologies.

Recommended high-level architecture:

```text
                 ┌──────────────────┐
                 │   Mobile App     │
                 │ Customer/Pro     │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │    Backend API   │
                 └────────┬─────────┘
                          │
             ┌────────────┼────────────┐
             ▼            ▼            ▼
        ┌─────────┐  ┌─────────┐  ┌──────────┐
        │Database │  │   AI    │  │ Storage  │
        └─────────┘  └─────────┘  └──────────┘
             │
             ▼
        ┌─────────────┐
        │ Notifications│
        └─────────────┘
```

---

## 👥 User Roles

### CUSTOMER

Requests and manages services.

### PROFESSIONAL

Provides services and manages jobs.

### ADMIN

Manages the platform, users, verification, disputes, and moderation.

Authorization must always be enforced on the backend.

---

## 📂 Project Structure

Document the actual project structure here.

Example:

```text
project/
├── mobile/
├── backend/
├── database/
├── docs/
├── tests/
└── README.md
```

> Replace this section with the real structure of the repository.

---

## ⚙️ Installation

Clone the repository:

```bash
git clone <repository-url>
cd <project-folder>
```

Install dependencies according to the actual project configuration.

Create the required environment variables:

```env
# Database
DATABASE_URL=

# Authentication
AUTH_SECRET=

# AI
AI_API_KEY=

# Storage
STORAGE_URL=

# Payments
PAYMENT_SECRET=
```

> Only include environment variables that are actually required by the project. Never commit real secrets.

---

## 🔑 Environment Variables

Never expose:

* API keys
* Authentication secrets
* Database credentials
* Payment secrets
* Private tokens

Use environment variables and secure secret management.

---

## 📸 Screenshots

Add screenshots of the main application experience:

* Home screen
* AI problem description
* AI classification
* Matching results
* Professional profile
* Appointment
* Job tracking
* Review
* Professional dashboard

---

## 🚧 Project Status

**Status:** 🚧 In Development

WASTA? is being developed as an ambitious mobile platform focused on AI-powered local service discovery and trusted professional matching in Algeria.

---

## 🔮 Future Vision

The long-term vision is to make WASTA? a trusted digital layer for everyday local services.

Instead of remembering dozens of phone numbers or depending entirely on personal connections, users could simply open the application and describe what they need.

```text
“I need someone to fix this.”

          ↓

        WASTA?

          ↓

Understand the problem

          ↓

Find the right professional

          ↓

Get the job done

          ↓

Build trust through real reputation
```

---

## 🌟 Core Idea

> **WASTA? — Find the right service, not the right person.**

The name intentionally plays with the familiar idea of **“wasta”** while proposing the opposite experience:

**You shouldn't need connections to find a good professional.**

You just need to describe the problem.

---

## 📄 License

Add the project's actual license here.

---

## 👨‍💻 Author

**Mehdi Koceir**

Computer Systems & Networks (SIR) Student
Full Stack Developer · React Native Developer · AI Automation

---

<p align="center">
  Built with the goal of making local services more accessible, trusted, and digital in Algeria 🇩🇿
</p>
