Feature: MediaController Endpoint

  Scenario Outline: Admin or Owner uploads media related to a vendor, service, or category
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a media file is uploaded with:
      | Upload Title | <title>        |
      | Related To   | <entityType>   |
      | Record ID    | <entityId>     |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | title              | entityType | entityId | expectedOutcome | expectedMessage    |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Vendor Image       | vendor     | 101      | 201             | vendor             |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Category Thumbnail | user       | 202      | 201             | user               |
      | adil.vendor@gmail.com   | password                | Test Image         | service    | 303      | 403             | Forbidden          |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | Missing File       | service    |          | 400             | File upload failed |

  Scenario Outline: View uploaded media for a specific record
    Given a record exists in the system with ID <entityId>
    When the user requests to view media associated with that record
    Then the system should return a list of media
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedContent>" in the result

    Examples:
      | entityId | expectedOutcome | expectedContent |
      | 1        | 200            | image/webp      |
      | 8        | 200            | image/webp      |
      | 9999     | 200            | []              |

  Scenario Outline: Authenticated users can download media by media ID
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When the user attempts to download the media file with ID <mediaId>
    Then the system should confirm the action was <expectedOutcome>
    And the response should contain the file bytes

    Examples:
      | email                   | password                | mediaId | expectedOutcome |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 1001    | 200             |
      | adil.vendor@gmail.com   | password                | 1002    | 200             |
      | adil.couple@gmail.com   | password                | 1003    | 200             |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 9999    | 404             |


  Scenario Outline: Admin deletes media
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to delete media with the record id of <mediaId>
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | expectedOutcome | expectedMessage | mediaId |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 204             | no_content      | 2153    |
      | adil.vendor@gmail.com   | password                       | 403             | Forbidden       | 2052    |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 404             | Not found       | 205200  |
