Feature: ServiceController Endpoint
  Scenario Outline: Browse wedding services using optional filters
    When a user searches for services with the following criteria:
      | search      | <search>      |
      | tagName     | <tagName>     |
      | location    | <location>    |
      | vendorId    | <vendorId>    |
      | rating      | <rating>      |
      | categoryId  | <categoryId>  |
      | priceStart  | <priceStart>  |
      | priceEnd    | <priceEnd>    |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | search      | tagName | location | vendorId | rating | categoryId | priceStart | priceEnd | expectedOutcome | expectedMessage |
      | Custom      |         | New York |          |        | 2          |            |          | 200            | Custom           |
      |             | Budget  |          |          |        |            |            |          | 200            | Budget Friendly  |
      |             |         | New York | 1        |        |            |            |          | 200            | Cake             |
      | Fake Search |         | Berlin   |          |        |            |            |          | 200            | content:[]       |


  Scenario Outline: Admin or vendor adds a new service
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When the vendor or admin submits a new service with the following details:
      | vendor_id    | <vendorId>    |
      | category_id  | <categoryId>  |
      | service_name | <serviceName> |
      | description  | <description> |
      | price_min    | <priceMin>    |
      | price_max    | <priceMax>    |
      | availability | <availability>|
      | location     | <location>    |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | vendorId | categoryId | serviceName          | description        | priceMin | priceMax | availability                   | location         | expectedOutcome | expectedMessage |
      | adil.vendor@gmail.com   | password                | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 201             | service_name    |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 201             | service_name    |
      | adil.couple@gmail.com   | password                | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 403             | Forbidden       |
      | adil.vendor@gmail.com   | password                | 199      | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 404             | Not found       |
      | adil.vendor@gmail.com   | password                | 1        | 299        | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 404             | Not found       |


  Scenario Outline: Admin or owner updates a service
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When the vendor or owner updates a service with record id of <serviceId> with the following details:
      | vendor_id    | <vendorId>    |
      | category_id  | <categoryId>  |
      | service_name | <serviceName> |
      | description  | <description> |
      | price_min    | <priceMin>    |
      | price_max    | <priceMax>    |
      | availability | <availability>|
      | location     | <location>    |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | serviceId | vendorId | categoryId | serviceName          | description        | priceMin | priceMax | availability                   | location         | expectedOutcome | expectedMessage |
      | adil.vendor@gmail.com   | password                | 2         | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 201             | service_name    |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 2         | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 201             | service_name    |
      | adil.couple@gmail.com   | password                | 2         | 1        | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 403             | Forbidden       |
      | adil.vendor@gmail.com   | password                | 2         | 199      | 2          | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 404             | Not found       |
      | adil.vendor@gmail.com   | password                | 2         | 1        | 299        | Luxury Bridal Makeup | Complete bridal... | 500      | 1000     | Available for bookings in 2025 | Atlanta, Georgia | 404             | Not found       |

  Scenario Outline: Admin deletes service
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to delete service with the record id of <serviceId>
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | expectedOutcome | expectedMessage | serviceId |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 204             | no_content      | 2153      |
      | adil.vendor@gmail.com   | password                | 403             | Forbidden       | 2052      |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 404             | Not found       | 205200    |