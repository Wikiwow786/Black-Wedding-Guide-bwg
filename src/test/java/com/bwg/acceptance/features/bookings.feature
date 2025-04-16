Feature: BookingController EndPoint

  Scenario Outline: A user views their bookings using optional search keywords or booking status
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When the user looks up their bookings by entering "<search>" as a keyword and selecting status "<status>"
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | search      | status    | expectedOutcome | expectedMessage |
      | adil.couple@gmail.com   | password                | adil        | pending   | 200             | adil            |
      | adil.couple@gmail.com   | password                | adil        | completed | 200             | adil            |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com |             | pending   | 200             | pending         |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | nonexistent |           | 200             | content:[]      |


  Scenario Outline: Couple creates a booking
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a couple creates a booking with the following details:
      | user_id    | <userId>    |
      | service_id | <serviceId> |
      | event_date | <eventDate> |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                 | password | serviceId | userId | eventDate            | expectedOutcome | expectedMessage |
      | adil.couple@gmail.com | password | 1         | 1212   | 2025-10-31T20:30:00Z | 201             | booking_id      |
      | adil.couple@gmail.com | password | 1         | 121222 | 2025-10-31T20:30:00Z | 404             | Not found       |
      | adil.couple@gmail.com | password | 122       | 1212   | 2025-10-31T20:30:00Z | 404             | Not found       |
      | adil.vendor@gmail.com | password | 1         | 1212   | 2025-10-31T20:30:00Z | 403             | Forbidden       |


  Scenario Outline: Admin or vendor updates a booking
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to update a booking with record id of <bookingId> with the following details:
      | status     | <status>    |
      | event_date | <eventDate> |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                 | password | bookingId | status    | eventDate            | expectedOutcome | expectedMessage |
      | adil.vendor@gmail.com | password | 101       | completed | 2025-10-31T20:30:00Z | 200             | booking_id      |
      | adil.vendor@gmail.com | password | 102       | pending   | 2025-10-31T20:30:00Z | 404             | Not found       |
      | adil.vendor@gmail.com | password | 103       | confirmed | 2025-10-31T20:30:00Z | 404             | Not found       |
      | adil.couple@gmail.com | password | 104       | cancelled | 2025-10-31T20:30:00Z | 403             | Forbidden       |


  Scenario Outline: Admin or vendor deletes a booking
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    When a call happens to delete a booking with record id of <bookingId>
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result

    Examples:
      | email                   | password                | expectedOutcome | expectedMessage | bookingId |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 204             | no_content      | 2153      |
      | adil.vendor@gmail.com   | password                | 204             | no_content      | 2052      |
      | adil.couple@gmail.com   | password                | 403             | Forbidden       | 2052      |
      | adilwaheed474@gmail.com | adilwaheed474@gmail.com | 404             | Not found       | 205200    |