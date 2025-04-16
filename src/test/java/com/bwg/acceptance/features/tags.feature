Feature: TagsController Endpoint

  Scenario Outline: Explore tags by typing what you're looking for
    When a user types "<keyword>" to explore available tags
    Then the response status should be <expectedStatus>
    And the response body should be "<responseContains>"

    Examples:
      | keyword     | expectedStatus | responseContains |
      | Luxury      | 200            | Luxury           |
      | Cake        | 200            | Cake             |
      | unknown-tag | 200            | content:[]       |

    Scenario Outline: User add tags to profile
      Given a logged-in user with the role "<role>"
      When a call happens to add tags with the following details:
        | tag_name | <tagName> |
        | status   | status    |
      Then the response status should be <expectedStatus>
      And the response body should be "<containsMessage>"

      Examples:
        | role        | tagName | expectedStatus | containsMessage |
        | ROLE_ADMIN  | Luxury  | 200            | tag_name        |
        | ROLE_ADMIN  | Luxury  | 409            | already exists  |
        | ROLE_COUPLE | Unknown | 403            | Forbidden       |

  Scenario Outline: Admin assigns a tag to a service
    Given a logged-in user with the role "<role>"
    When the admin assigns the tag with ID <tagId> to the service with ID <serviceId>
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | tagId | serviceId | expectedStatus | containsMessage |
      | ROLE_ADMIN  | 201   | 1010      | 200            | tag_id          |
      | ROLE_VENDOR | 201   | 1010      | 403            | Forbidden       |
      | ROLE_ADMIN  | 9999  | 1010      | 404            | Not found       |
      | ROLE_ADMIN  | 201   | 9999      | 404            | Not found       |


  Scenario Outline: Admin assigns a tag to a category
    Given a logged-in user with the role "<role>"
    When the admin assigns the tag with ID <tagId> to the category with ID <categoryId>
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | tagId | categoryId | expectedStatus | containsMessage |
      | ROLE_ADMIN  | 201   | 1010       | 200            | tag_id          |
      | ROLE_VENDOR | 201   | 1010       | 403            | Forbidden       |
      | ROLE_ADMIN  | 9999  | 1010       | 404            | Not found       |
      | ROLE_ADMIN  | 201   | 9999       | 404            | Not found       |

  Scenario Outline: Admin removes tag
    Given a logged-in user with the role "<role>"
    When a call happens to delete tag with the record id of <tagId>
    Then the response status should be <expectedStatus>
    And the response body should be "<containsMessage>"

    Examples:
      | role        | expectedStatus | containsMessage | tagId  |
      | ROLE_ADMIN  | 204            | no_content      | 2153   |
      | ROLE_VENDOR | 403            | Forbidden       | 2052   |
      | ROLE_ADMIN  | 404            | Not found       | 205200 |
