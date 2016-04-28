Feature: 
  http://www.which.co.uk/reviews/televisions testing

  Background: 
    Given I have navigated to http://www.which.co.uk/reviews/televisions

  Scenario: Landing Page should be populated with results and empty filters
    Then I should see more then one search results
    And Results count should be visible
    And Pagination should be visible and correct
    And Sort dropdown should be set to 'Most-recently tested'

  Scenario: Clear filters should clear all side filters
    When I select 3 random filters
    And I click clear filters
    Then Filters should be empty

  Scenario: Results can be order by price (low to high)
    When I order results by "Price (low to high)"
    Then Result should be ordered by price low to high

  Scenario: Users are able to filter by screen size
    When I select screen size to be 47-55"
    Then I should see search results for 47-55" listings

  Scenario: Users are able to filter by price
    When I select prices from 500 to 1,200
    Then I should see only results in price range 500-1200
    And Results count should be visible

  @ignore
  Scenario: Users are able to choose filter by Brands
    When I select Samsung
    Then I should see results only for Samsung
    And Results count should be visible

  @ignore
  Scenario: Users are able to select Brands show more option
    When I select Show more option
    Then I should see more brand options

  @ignore
  Scenario: Users are able to select Brands Show Fewer... option
    When I select Show Fewer... option
    Then I should see less results

  @ignore
  Scenario: User are able to choose Screen type
    When I select LED
    Then I should see results for LED Screen type
    And Results count should be visible

  @ignore
  Scenario: Users are able to choose Resolution
    When I select Full HD
    Then I should see results for 4K ultra HD Resolution type

  @ignore
  Scenario: Users are able to choose features
    When I select Smart TV
    Then I should see results for Smart Tv features

  @ignore
  Scenario: Users are able to choose Retailers
    When I select Amazon Marketplace UK
    Then I should see at least one result
