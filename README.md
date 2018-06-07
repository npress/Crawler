# Main class:
Crawler.java
I created a jar that you can run for your convenience at the top-level of the project directory.
`Nemas-iMac-3:~ npress$ java -jar /Users/npress/IdeaProjects/Crawler/Crawler.jar`

# Assumptions:
- A response with status code 200 is successful.  Any other status code is
considered a failure and is printed to the screen.
- I am assuming https protocol for my links, as I'm using HttpsURLConnection to make the
connection.

# Features:
- The default maximum number of unique links to connect to is 10,000.  The user has the option
 to set this number as the first argument to the java program.


