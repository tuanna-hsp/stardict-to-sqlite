##Usage
- Extract stardict files, put `.ifo`, `.dict`, `.idx` and `.syn` (if available) in a same folder.
- Open any of those files using the tool, then press `Convert`.

Note: Some files maybe compressed, if you see something like `filename.dict.dz`, extract these files first (use [7-Zip](http://www.7-zip.org/download.html)).

##SQLite database
There are two tables:
- **main**(*id, word, meaning*) table stores words and their definition from .dict and .idx files.
- **syn**(*synonym, word_id*) table stores synonyms and their references to `main` table.

##License
[The Unlicense](https://github.com/tuanna-hsp/stardict-to-sqlite/blob/master/LICENSE)
