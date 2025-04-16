Feature: User Login

  Scenario Outline: User logs in with different credentials
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    Then the system should confirm the action was <expectedOutcome>
    And the response <containsToken>

    Examples:
      | email                 | password  | expectedOutcome | containsToken      |
      | adil.vendor@gmail.com | password         | 200            | should contain JWT |
      | fake.user@gmail.com   | wrongPass | 400            | should not contain |

