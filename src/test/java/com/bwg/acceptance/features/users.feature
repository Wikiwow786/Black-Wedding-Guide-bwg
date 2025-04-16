Feature: UserController Endpoint

  Scenario Outline: Admin reviews user accounts by name or association
    Given a logged-in user with the role "<role>"
    When the admin checks user accounts using:
      | Name keyword | <search>   |
      | User ID      | <userId>   |
      | Vendor ID    | <vendorId> |
    Then the response status should be <expectedStatus>
    And the response body should be "<responseContains>"

    Examples:
      | role       | search  | userId | vendorId | expectedStatus | responseContains |
      | ROLE_ADMIN | adil    |        |          | 200            | adil             |
      | ROLE_ADMIN |         | 1001   |          | 200            | user_id          |
      | ROLE_ADMIN |         |        | 2001     | 200            | vendor_id        |
      | ROLE_ADMIN | nomatch |        |          | 200            | content:[]       |


  Scenario Outline: Logged-in users can access their profile details
    Given a logged-in user with the role "<role>"
    When they view their personal profile information
    Then the response status should be <expectedStatus>
    And the response body should be "<responseContains>"

    Examples:
      | role        | expectedStatus | responseContains |
      | ROLE_ADMIN  | 200            | user_id          |
      | ROLE_VENDOR | 200            | user_id          |
      | ROLE_COUPLE | 200            | user_id          |

  Scenario Outline: User updates a profile
    Given a logged-in user with the role "<role>"
    When a call happens to update user details with id of <userId> with the following details:
      | first_name   | <firstName>   |
      | last_name    | <lastName>    |
      | email        | <email>       |
      | phone_number | <phoneNumber> |
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | userId | firstName | lastName | email                   | phoneNumber  | expectedStatus | containsMessage |
      | ROLE_ADMIN  | 101    | Amina     | Brown    | amina.brown@example.com | +15551234567 | 200            | first_name      |
      | ROLE_VENDOR | 102    | David     | Lee      | david.lee@example.com   | +15557654321 | 200            | first_name      |
      | ROLE_COUPLE | 103    | Olivia    | Johnson  | olivia.j@example.com    | +15550987654 | 200            | first_name      |
      | ROLE_VENDOR | 999    | Test      | User     | amina.brown@example.com | +15550000000 | 400            | already exists  |
      | ROLE_COUPLE | 102    | Alice     | Kim      | david.lee@example.com   | +15551231234 | 403            | Forbidden       |


  Scenario Outline: Admin deletes user
    Given a logged-in user with the role "<role>"
    When a call happens to delete user with the record id of <userId>
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | expectedStatus | containsMessage | userId |
      | ROLE_ADMIN  | 204            | no_content      | 2153   |
      | ROLE_VENDOR | 403            | Forbidden       | 2052   |
      | ROLE_ADMIN  | 404            | Not found       | 205200 |