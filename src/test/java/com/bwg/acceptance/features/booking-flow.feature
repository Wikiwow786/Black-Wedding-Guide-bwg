Feature: Seamless Wedding Booking Journey
  As a couple planning our wedding,
  I want to explore services, select the perfect one, and book it effortlessly,
  So that I can enjoy a smooth experience on the Black Wedding Guide platform.

  Scenario Outline: Couple logs in, explores services, selects one, and books it
    Given a registered user with email "<email>" and password "<password>"
    When the user logs into the system with valid credentials
    And the user searches for services using the keyword "<search>"
    And the user selects a service with ID <serviceId> and name "<serviceName>"
    And the user books the selected service using:
      | user_id    | <userId>    |
      | service_id | <serviceId> |
      | event_date | <eventDate> |
    Then the system should confirm the action was <expectedOutcome>
    And the user should see "<expectedMessage>" in the result


    Examples:
      | email                 | password |  | search   | serviceName           | eventDate            | expectedOutcome | expectedMessage | serviceId | userId |
      | adil.couple@gmail.com | password |  | Custom   | Custom Wedding Cake   | 2025-12-15T18:00:00Z | 201             | booking_id      | 1         | 1212   |
      | adil.couple@gmail.com | password |  | Catering | Full Wedding Catering | 2025-11-22T13:30:00Z | 201             | booking_id      | 2         | 1212   |
      | adil.vendor@gmail.com | password |  | Catering | Full Wedding Catering | 2025-11-22T13:30:00Z | 403             | Forbidden       | 2         | 1212   |
