Feature: Air Quality Searches

  Background: Search Tests
    Given Open Browser

  # Using Search Bar
  Scenario: Search By Existent City (SearchBar)
    When I navigate to 'http://localhost:8081/'
    And I write 'Paris' in the searchbox
    And clicks on GO 1
    Then a div containing the city airquality info with city name 'Paris' should appear
    And the city's Latitude should be 48.86
    And the city's Longitude should be 2.35
    And the Date should be 'Today'

  Scenario: Search By Existent City And Choose Tomorrow as Date (SearchBar)
    When I navigate to 'http://localhost:8081/'
    And  I write 'Paris' in the searchbox
    And selects 'Tomorrow' as Date
    And clicks on GO 1
    Then a div containing the city airquality info with city name 'Paris' should appear
    And the city's Latitude should be 48.86
    And the city's Longitude should be 2.35
    And the Date should be 'Tomorrow'

  Scenario: Search By Non Existent City (SearchBar)
    When I navigate to 'http://localhost:8081/'
    And  I write 'blablabla' in the searchbox
    And clicks on GO 1
    Then an error message saying 'City not found.' should appear 1



  #Using Select Bar
  Scenario: Search By Existent City (SelectBar)
    When I navigate to 'http://localhost:8081/'
    And  I clicks on 'Paris' in the select bar
    And clicks on GO 2
    Then a div containing the city airquality info with city name 'Paris' should appear
    And the city's Latitude should be 48.86
    And the city's Longitude should be 2.35
    And the Date should be 'Today'

  Scenario: Search By Existent City And Choose Tomorrow as Date (SelectBar)
    When I navigate to 'http://localhost:8081/'
    And  I clicks on 'Paris' in the select bar
    And selects 'Tomorrow' as Date
    And clicks on GO 2
    Then a div containing the city airquality info with city name 'Paris' should appear
    And the city's Latitude should be 48.86
    And the city's Longitude should be 2.35
    And the Date should be 'Tomorrow'

  Scenario: Search By Non Existent City (SelectBar)
    When I navigate to 'http://localhost:8081/'
    And  I leave the default option 'Cities'
    And clicks on GO 2
    Then an error message saying 'City not found.' should appear 2



    # Using Coordinates
  Scenario: Search By Coords
    When I navigate to 'http://localhost:8081/'
    And  the user writes '40.71' as Latitude
    And the user writes '-74' as Longitude
    And clicks on GO 3
    Then a div containing the city airquality info with city name 'New York' should appear
    And the city's Latitude should be 40.71
    And the city's Longitude should be -74.01
    And the Date should be 'Today'

  Scenario: Search By Coords and Date
    When I navigate to 'http://localhost:8081/'
    And  the user writes '40.71' as Latitude
    And the user writes '-74.01' as Longitude
    And selects 'Tomorrow' as Date
    And clicks on GO 3
    Then a div containing the city airquality info with city name 'New York' should appear
    And the city's Latitude should be 40.71
    And the city's Longitude should be -74.01
    And the Date should be 'Tomorrow'

  Scenario: Search By Wrong Coords Input
    When I navigate to 'http://localhost:8081/'
    And  the user writes 'abc' as Latitude
    And the user writes 'sdads' as Longitude
    And clicks on GO 3
    Then an error message saying 'Coords must be numbers.' should appear 3



   # Dynamism Test
  Scenario: Doing 2 searches
    When I navigate to 'http://localhost:8081/'
    And I do a search on an existent city
    And I do another search on another existent city
    Then div should have 2 result cards


  Scenario: Doing 2 searches and deleting 1
    When I navigate to 'http://localhost:8081/'
    And I do a search on an existent city
    And I do another search on another existent city
    And I delete one of them
    Then div should have 1 result cards

  Scenario: Doing 2 searches and delete all
    When I navigate to 'http://localhost:8081/'
    And I do a search on an existent city
    And I do another search on another existent city
    And I delete all
    Then div should have 0 result cards

