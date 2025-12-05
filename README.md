# 🧪 Swag Labs – Manual & Automation Testing Project

<p align="center">
  <img src="https://i.ytimg.com/vi/hypO6hgSzP4/sddefault.jpg" width="450"/>
</p>

## 📌 Overview
This project demonstrates a complete **Software Testing lifecycle** (Manual + Automation) on the **Swag Labs (saucedemo.com)** application.  
It includes structured manual test cases and a full automation framework using **Java – Selenium – TestNG – Maven – POM**.

---

## 🧱 Tech Stack
| Category | Tools |
|---------|-------|
| Programming Language | Java |
| Automation | Selenium WebDriver |
| Test Framework | TestNG |
| Build Tool | Maven |
| Design Pattern | Page Object Model (POM) |
| IDE | IntelliJ IDEA |
| Manual Testing | Test Cases, Bug Reports, Scenarios |

---

## 📁 Project Structure
```
Test-Automation/
│
├── org.example
│   └── Main
│
└── Swag
    ├── LoginData.java          # Test data (valid/invalid login)
    ├── LoginPage.java          # Page Object for Login Page
    ├── ProductsPage.java       # Page Object for Products Page
    └── ProductsPageTest.java   # TestNG test cases for Products Page
    ├── checkoutinfo.java
    ├── checkoutoverview.java 
    ├── checkoutcomplete.Java 
```

---

## 📝 Manual Testing Coverage
✔️ Functional Test Cases  
✔️ UI/UX Validation  
✔️ Error Message Verification  
✔️ Input Validation Scenarios  
✔️ Add-to-Cart + Sorting Test Scenarios  
✔️ End-to-End Flow Testing  

Manual testing files include:
- Login Test Cases  
- Products Page Test Cases
- checkout Test cases  
- Test Scenarios  
- Expected vs Actual Results 
---

## 🤖 Automation Testing Coverage

### 🔐 Login Page
- Valid login  
- Invalid login (wrong username/password)  
- Locked user validation  
- Empty username/password validation  
- Assertion of displayed error messages  

### 🛒 Products Page
- Validate product list  
- Add to cart functionality  
- Product name & price assertions  
- Sorting tests (A → Z, Z → A, Low → High, High → Low)  
- Buttons / UI element checks

  
### check out 
- Valid checkout
- Required fields validation
- Error messages
- Price & totals validation
- Verify items in overview
- Continue / Cancel actions
- Complete order
- Confirm success page

---

## ▶️ How to Run the Automation Tests

### 1️⃣ Clone the project
```bash
git clone https://github.com/HAGER898/Final_project_swag_labs.git
```

### 2️⃣ Install Maven dependencies
```bash
mvn clean install
```

### 3️⃣ Run tests with TestNG
```bash
mvn test
```

---



---

## 🧑‍🤝‍🧑 Team Members
| Name | Role |
|------|------|
| **Ahmed Abo Elsuad** | QA Engineer |
| **Ahmed Yousry** | QA Engineer |
| **Hager Hussien** | QA Engineer |

---

## ⭐ If you like this project
Please consider starring ⭐ the repository — it motivates us to contribute more!

---

## 📄 License
This project is for educational and training purposes.
