# Main class:
Crawler.java
<br>I created a jar that you can run for your convenience at the top-level of the project directory.
<br>`Nemas-iMac-3:~ npress$ java -jar /Users/npress/IdeaProjects/Crawler/Crawler.jar`

# Assumptions:
- I use the remote file located at the following address to read the seed links from.<br>
https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json
<br>If this file is not present, the program will not run properly.  Just in case the original disappears, I copied the data.json file into the root directory of the project.
- A response with status code 200 is successful.  Any other status code is
considered a failure and is printed to the screen.
- I am assuming https protocol for my links, as I'm using HttpsURLConnection to make the
connection.

# Features:
- The default maximum number of unique links to connect to is 10,000.  The user has the option
 to set this number as the first argument to the java program.


