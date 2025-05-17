# Tamil Rhyme Finder and Phrase Generator

The service is live at:
👉 [https://tamilrhymefinder.onrender.com](https://tamilrhymefinder.onrender.com)

Please note: since this is hosted on a free-tier platform, the server may take a little time to wake up if it's been inactive for a while — this is just the container spinning back up. The first load might be slow, but once it’s running, performance will be smooth and snappy until it goes idle again.

## Table of Contents

1. [Introduction](#introduction)

   * [Why I Made This](#why-i-made-this)
   * [Challenges I Faced](#challenges-i-faced)
2. [Getting Started](#getting-started)

   * [Running the Code](#running-the-code)
3. [Features](#features)
4. [Future Plans](#future-plans)
5. [Contributing](#contributing)
6. [Conclusion](#conclusion)

---

## Introduction

Welcome to the Tamil Rhyme Finder and Phrase Generator — a creative tool built for lyricists, poets, and music producers working in Tamil, one of the world's oldest and most expressive languages. With over 80 million speakers globally, Tamil deserves tools that support its richness — especially in creative spaces like songwriting.

As someone who produces music and writes lyrics, I often hit a wall when trying to find the perfect Tamil word to rhyme or fit rhythmically. While English tools abound, Tamil resources are few and far between. This project is my attempt to fill that gap.

---

## Why I Made This

Writing lyrics in Tamil is deeply rewarding — but not always easy. I wanted a tool that could help me, and others like me, find rhymes, discover lyrical phrases, and get inspired by how certain words have been used before.

Existing English tools made this process effortless. But for Tamil? There just wasn’t anything that felt intuitive, comprehensive, or even usable. So I decided to build one.

---

## Challenges I Faced

Let me walk you through the technical decisions and the story behind the code — especially if some parts look over-engineered at first glance. There were good reasons for every twist and turn.

### Scraping Words and Phrases

The initial dataset came together smoothly. With the help of BeautifulSoup and a bit of Selenium magic, I scraped a rich collection of Tamil words and phrases — the foundation for everything that followed.

### Server Module: Data Storage & Retrieval

Once the dataset was in place, the next challenge was storing and accessing it efficiently.

#### Outgrowing Free SQL

I started with a free SQL database, but quickly hit storage limits. So I pivoted to using a hashmap — with rhymes as keys and the corresponding word lists as values. On my local machine, this was blazing fast (O(1) lookup speed) and worked like a charm.

#### Trouble on Free Hosts

But when I deployed it to platforms like Heroku and Render, it fell apart. Their limited memory meant the hashmap-heavy approach just wouldn’t run. The server wouldn’t even start.

#### MapDB to the Rescue

To tackle memory issues without giving up performance, I experimented with custom file formats, but they lacked flexibility. That’s when I found **MapDB** — a game-changer.

MapDB offered the right mix of speed, memory efficiency, and persistence. I rewrote the storage layer using it, and everything clicked again... until I hit another wall.

#### Splitting the Load

The MapDB file grew too large to upload. So I split it — one file per Tamil alphabet. This modular approach not only solved the upload issue but also improved data management. Finally, deployment was a success, and the server ran smoothly with fast response times and lean memory usage.

---

## Further Evolution of the Code

Even after optimizing with MapDB, I realized long-term sustainability needed something more robust — especially for free hosting. So I shifted the entire storage responsibility to an external database.

### Enter MongoDB

MongoDB’s generous free tier made it a natural fit. I tossed out the previous server code and rebuilt it using a clean **Controller-Repository-Service** design. The result? A much more maintainable and scalable backend.

This time, I also introduced advanced features to support more nuanced word exploration — going beyond basic rhyming. These include:

* **Phonetically Similar Words**: Helps find words that *sound* alike, even if they don’t technically rhyme.
* **Consonant-Vowel Pattern Matching**: Useful for identifying rhythmic or structural similarities in lyrics.
* **Soundex and Metaphone Codes**: Algorithms that surface phonetically similar Tamil words based on pronunciation logic.
* **Syllable Count**: Essential for keeping lyrical meter consistent.
* **Stress Pattern Analysis**: Helps match the flow and emphasis across lines.

These additions significantly enhance the tool’s creative utility, making it more than just a rhyme finder — it’s now a full-fledged linguistic assistant for Tamil songwriting.

### No Spring Boot or External Frameworks — by Necessity

To ensure the server could run smoothly on free hosting services with very limited RAM and CPU, I avoided using Spring Boot or any external frameworks. This wasn’t a critique of those tools — they’re excellent for larger, more resource-rich environments. But in this case, the priority was minimal resource consumption. A lean, custom setup allowed the server to stay performant within tight hosting constraints.

---

## Getting Started

Want to try it out? Here's how to run the project locally:

1. Clone the repository.
2. Navigate into the project directory.
3. Run:

```bash
docker-compose up
```

The tool will be available on `localhost:8080`.

---

## Features

* **Rhyme Finder**: Find rhyming words for any given Tamil word — ideal for writing verses that flow.
* **Phrase Generator**: Discover meaningful, poetic Tamil phrases built around your chosen word.
* **Phonetically Similar Word Finder**: This can come handy to optimise flow of the verses.
* **Rich Dataset**: Built on a carefully curated dataset of Tamil words and expressions.
* **Simple UI**: The interface is clean and intuitive — no distractions, just tools that work.

---

## Future Plans

This is just the beginning. I plan to expand support to other Indian languages, opening up new creative possibilities for artists and writers across the subcontinent. More features and user-driven improvements are also on the horizon.

---

## Contributing

Got ideas? Found a bug? Contributions are welcome!

Feel free to open an issue or submit a pull request. Just make sure to follow the existing coding standards.

---

## Conclusion

Building this tool taught me more than just code — it was a lesson in creative problem-solving, patience, and pushing through roadblocks.

The Tamil Rhyme Finder and Phrase Generator is more than a coding project. It’s a small step toward supporting a beautiful language in a creative context. I hope it inspires your next song, poem, or verse.

Thanks for stopping by.

— *Kabilan Baskaran*
