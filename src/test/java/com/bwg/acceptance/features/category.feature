Feature: CategoryController EndPoint

  Scenario Outline: Explore vendor categories using keyword or tag filters
    When a user explores categories by searching for "<search>" and filtering with tag "<tagName>"
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | search     | tagName         | expectedOutcome | expectedMessage |
      | Cake       |                 | 200            | Cake             |
      | Caterers   | Budget Friendly | 200            | Caterers         |
      | FakeSearch |                 | 200            | content:[]       |


  Scenario Outline: Admin submits a new category for vendors
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a category with the name "<categoryName>" is submitted
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | categoryName     | expectedOutcome | expectedMessage |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Luxury Cakes     | 201             | category_name   |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Luxury Cakes     | 409             | already exists  |
      | adil.vendor@gmail.com   | password                | Wedding Planners | 403             | Forbidden       |

  Scenario Outline: Admin updates category for vendors
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to update category with the record id of <categoryId> and category name of "<categoryName>"
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | categoryName     | expectedOutcome | expectedMessage | categoryId |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Luxury Cakes123  | 200             | category_name   | 2052       |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Luxury Cakes123  | 409             | already exists  | 2052       |
      | adil.vendor@gmail.com   | password                | Wedding Planners | 403             | Forbidden       | 2052       |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Wedding Planners | 404             | Not found       | 205200     |


  Scenario Outline: Admin deletes category for vendors
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to delete category with the record id of <categoryId>
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | expectedOutcome | expectedMessage | categoryId |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 204             | no_content      | 2153       |
      | adil.vendor@gmail.com   | password                | 403             | Forbidden       | 2052       |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 404             | Not found       | 205200     |



