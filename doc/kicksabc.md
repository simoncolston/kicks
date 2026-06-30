# Text Notation `kicksabc`

## General Format

Starts with a header, followed by a blank line, followed by multiple note lines and lyric lines.

## Header

The header contains general information about the song.
Each line is headed by a command.

| Command | Meaning | Values                          | Default if not present | Example       |
|---------|---------|---------------------------------|------------------------|---------------|
| `T`     | Title   | String                          | Required command       | `T:安里屋ユンタ`    |
| `K`     | Tuning  | `honchoshi`, `sansage`, `niage` | `honchoshi`            | `K:honchoshi` |
| `W`     | Script  | `katakana`, `romaji`            | `katakana`             | `W:romaji`    |

## Note lines

These are not preceded by a command and start after a blank line after the header.

### Note modifiers

Note size:    `l` =  large, `s` = small (default: `l`. But `s` for notes on a cell boundary)

Accidental:   `b` = flat

Articulation: `'` = hammer (uchi utu), `^` = upstroke (kaki utu)

Finger:       `f1`, `f2`, `f3`, `f4`

Slur:         `(` = start, `)` = end. e.g. `( 33 34 )`

Chord:        braces e.g. `{ 20 30 }`

*Note:* modifiers come directly after the note number, in any order 
e.g. `21^sf2` is 上 with upstroke, small size and finger 2.

### Note position

By default, each note is set in the centre of a cell.
The note can be given an absolute position in the cell by adding the modifier `<n>`, 
where `n` is a number between 1 and 12.

E.g.

`21<6>` would be 上 in the centre of the cell (default)

`20<3>` would be 四 one quarter of the way down the cell (3/12)

`22<12>` would be 中 at the end of the cell (on the line).
A shortcut for this is to precede the note with a full-stop `.`, e.g. `.20` = `20<12>`

### Others

Repeats:      `[` = start repeat, `]` = end repeat

## Word lines

The syllables start their alignment with the notes in the line above.
To add a space use `*`.
The position of the syllable can be controlled in the same way as for notes.  E.g. `sa<3>` or `.sa`

## Example

```
T:安里屋ユンタ
K:honchoshi

[ 22 30 33 10 33 10 33 33 31 30 20 21 22 30 10 30 10 31 30 00
20 10.20 21 10 21 22.21^ 10 30 10 11 20 22 21 13 20 00 ]

20 10.20 21 10 22 30 22 10.20 21 10 20 10.20 21 22 30 10 30 31 30 10
sa * ki mi ha no na ka no * i.ba ra nu ha * na * ka

```

