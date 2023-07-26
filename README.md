# Tamil Rhyme Finder and Phrase Generator

## Table of Contents

1. [Introduction](#introduction)
   - [Why I made this?](#why-i-made-this)
   - [Challenges faced when writing this code](#challenges-faced-when-writing-this-code)
2. [Getting Started](#getting-started)
   - [Running the Code](#running-the-code)
3. [Features](#features)
4. [Future Plans](#future-plans)
5. [Contributing](#contributing)
6. [Conclusion](#conclusion)



## Introduction
Welcome to the Tamil Rhyme Finder and Phrase Generator! This tool is designed for music producers, poets, and creative minds who love writing lyrics in Tamil, one of the world's oldest languages, spoken by over 80 million people worldwide. As a music producer and passionate lyricist, I encountered challenges in finding Tamil words that rhymed well and flowed seamlessly in my songs. Additionally, I yearned for inspiration from past songs that used certain words creatively, fueling my own writing process.

## Why I made this?
As a music producer, I wanted to express my creativity through song lyrics in Tamil, a language rich in history and culture. However, I struggled to find Tamil words that fit my rhyming and rhythmic requirements. Existing tools offered plenty of options for the English language, but there was a glaring lack of resources for Tamil.

To address this void and cater to fellow Tamil lyricists, I embarked on a journey to build a dedicated Tamil Rhyme Finder and Phrase Generator. My goal was to provide a user-friendly platform that empowered artists to explore a vast array of rhymes and phrases in Tamil, enhancing their songwriting process.

## Challenges faced when writing this code

In this section, we'll delve into the intricacies of the code design and architecture behind the tool. While the project aims to provide a robust and user-friendly tool for Tamil lyricists, you might have noticed aspects of the code that appear unusual or seemingly over-engineered. We'll address these observations and shed light on the reasoning behind the choices made during development. Let me tell me the story of how I wrote the code.

## Scraping Words and Phrases

As a seasoned Selenium expert, scraping words and phrases from various sources was relatively smooth, thanks to the power of BeautifulSoup. Within a few hours, I was able to collect a rich dataset of Tamil words and phrases, which formed the backbone of the rhyme finder and phrase generator.

## Server Module: Efficient Data Storage and Retrieval
The server module of the Tamil Rhyme Finder and Phrase Generator is a critical component responsible for efficiently storing and retrieving a vast dataset of words, rhymes, and phrases. Building this module presented several challenges, but with determination and creativity, I found a robust solution to ensure both performance and scalability.

## Initial Hurdles
As the project's word database grew significantly with words, rhymes, and phrases in Tamil, relying on a free online SQL database was no longer feasible due to its storage limitations. To address this constraint, I decided to implement a custom solution using a hashmap.

## Hashmap-Based Storage
I swiftly developed code using a hashmap to store the possible rhymes as keys and the corresponding words with those rhymes as values. This implementation demonstrated remarkable performance and retrieval speed, operating at O(1) complexity. On my local machine, the code worked seamlessly, providing quick access to the relevant data.

## Deployment Challenges
However, the real challenge arose when deploying the code to free hosting servers like heroku.com and render.com. Upon running the server on these platforms, I encountered a surprising setback—the server wouldn't even start. It became evident that the hashmap-based approach required a more optimized and memory-efficient solution.

## Striving for Efficiency
To make the server more efficient and overcome memory constraints, I needed to rethink the data storage strategy while retaining O(1) retrieval performance. I explored various alternatives, such as storing the data in a file with a custom format. However, I found these solutions lacking, as they either compromised performance or fell short in handling the massive dataset.

## MapDB to the Rescue
After persistent efforts, I discovered a lifesaver—MapDB. Leveraging MapDB as the underlying storage mechanism transformed the project. MapDB provided the optimal balance between performance, memory usage, and data integrity. I rewrote the code using MapDB, and once again, it worked flawlessly on my local machine.

## Dealing with Large Files
However, when I attempted to push the code to the hosting server, a new challenge emerged. The MapDB file containing the entire dataset became too large for the hosting environment. To resolve this, I devised a clever approach by supporting multiple MapDB files, each storing data for a specific alphabet. By segmenting the data this way, I could efficiently manage the data without encountering size limitations.

## Successful Deployment
Finally, with the implementation of multiple MapDB files, I successfully pushed the optimized code to the hosting server. The server now boasts excellent performance, rapid data retrieval, and streamlined memory utilization, all essential for serving users seamlessly.

## Getting Started
Before you dive into exploring Tamil rhymes and phrases, follow these steps to get the code up and running.

## Running the Code
Clone this repository to your local machine.

Navigate to the project directory.

Execute the following command:

Copy code
docker-compose up
The Tamil Rhyme Finder and Phrase Generator will now be accessible on port 8080.

## Features
Rhyme Finder: Quickly find rhyming words for your chosen Tamil word, aiding your songwriting process.

Phrase Generator: Discover creative phrases and expressions using a given Tamil word, adding depth to your lyrics.

Rich Database: Benefit from a comprehensive database of Tamil words and phrases, meticulously curated for aspiring lyricists.

User-Friendly Interface: Enjoy a simple and intuitive interface that ensures a smooth user experience.

## Future Plans
I envision expanding the Tamil Rhyme Finder and Phrase Generator to encompass a wide variety of Indian languages, catering to a broader audience of songwriters and poets. Additionally, I plan to incorporate user feedback and suggestions to enhance the tool's features and usability continually. Together, we can create a dynamic platform that nurtures creativity in language and music.

## Contributing
Contributions to this project are welcome and greatly appreciated. If you have ideas for new features, improvements, or bug fixes, feel free to submit a pull request. Please ensure to follow the established coding standards and practices.

## Conclusion

This journey of building the server module was an enlightening experience. It taught me the value of adaptability, creativity, and the persistence required to overcome complex challenges in software development. The efficient data storage and retrieval system ensures that the Tamil Rhyme Finder and Phrase Generator offers users a delightful experience while exploring the rich world of Tamil lyrics.

Thank you for taking an interest in the Tamil Rhyme Finder and Phrase Generator! I hope this tool becomes a valuable asset in your creative journey. Should you have any questions, feedback, or ideas, do not hesitate to reach out. Happy songwriting!

\[Kabilan Baskaran\]

