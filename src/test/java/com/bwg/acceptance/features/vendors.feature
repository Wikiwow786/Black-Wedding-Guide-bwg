Feature: VendorController EndPoint

  Scenario Outline: Explore vendors by typing what you're looking for
    When a user types "<keyword>" to explore available vendors
    Then the response status should be <expectedStatus>
    And the response body should be "<responseContains>"

    Examples:
      | keyword        | expectedStatus | responseContains |
      | San Francisco  | 200            | San Francisco    |
      | Cake           | 200            | cake             |
      | unknown-vendor | 200            | content:[]       |


  Scenario Outline: Vendor creates a profile with business details
    Given a logged-in user with the role "<role>"
    When a vendor submits a record with the following details:
      | user_id       | <userId>         |
      | business_name | <businessName>   |
      | location      | <location>       |
      | description   | <description>    |
      | rating        | <rating>         |
      | total_reviews | <totalReviews>   |
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | userId | businessName                | location          | description                                                         | rating | totalReviews | expectedStatus | containsMessage |
      | ROLE_VENDOR | 301    | Melanin Moments Photography | Atlanta, Georgia  | Capturing love stories for Black couples with elegance and passion. | 4.8    | 230          | 201            | business_name   |
      | ROLE_VENDOR | 302    | Royal Essence Cakes         | Chicago, Illinois | Custom luxury wedding cakes celebrating Black culture and heritage. | 4.9    | 180          | 201            | business_name   |
      | ROLE_COUPLE | 303    | Soulful Harmony Music Co.   | Houston, Texas    | Live soulful performances for modern Black weddings.                | 5.0    | 75           | 403            | Forbidden       |


  Scenario Outline: Vendor updates a profile with business details
    Given a logged-in user with the role "<role>"
    When a call happens to update business details with id of <vendorId> with the following details:
      | business_name | <businessName> |
      | location      | <location>     |
      | description   | <description>  |
      | total_reviews | <totalReviews> |
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | businessName                | location          | description                                                         | totalReviews | expectedStatus | containsMessage | vendorId |
      | ROLE_VENDOR | Melanin Moments Photography | Atlanta, Georgia  | Capturing love stories for Black couples with elegance and passion. | 230          | 201            | business_name   | 1001     |
      | ROLE_VENDOR | Royal Essence Cakes         | Chicago, Illinois | Custom luxury wedding cakes celebrating Black culture and heritage. | 180          | 201            | business_name   | 1002     |
      | ROLE_COUPLE | Soulful Harmony Music Co.   | Houston, Texas    | Live soulful performances for modern Black weddings.                | 75           | 403            | Forbidden       | 1003     |


  Scenario Outline: Admin deletes vendor
    Given a logged-in user with the role "<role>"
    When a call happens to delete vendor with the record id of <vendorId>
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | expectedStatus | containsMessage | vendorId |
      | ROLE_ADMIN  | 204            | no_content      | 2153     |
      | ROLE_VENDOR | 403            | Forbidden       | 2052     |
      | ROLE_ADMIN  | 404            | Not found       | 205200   |