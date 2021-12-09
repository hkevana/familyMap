# familyMap
An application that shows users their genealogy 

client - built to run with Android Studio, utilizes Android Studio built in widgets and background threading

server - API built to handle all neccesary request for the Family Map Application
  /user/register  - regesters a new user
  /user/login     - login an existing user
  /clear          - clears existing data of current user (used in testing)
  /fill           - fills geneaology data for current user, can specify how many generations (userd in testing)
  /load           - load specific family geneaology information for a specific user
  /person         - find a person based off their unique identifier
  /event          - find family of people associated with a given event based off its unique identifier
  /               - general API endpoint that direct to a basic testing website
