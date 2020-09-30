# MusicAnalysis
> A project dedicated towards the visualization, analysis and feature extraction of Music using ML techniques.

# Project Structure

- GUI
- Machine Learning Model
- Music Feature Extraction

# Libraries / Dependencies
> See requirements.txt
- [ChordDroid](https://github.com/trungdq88/ChordDroid)

## Python
- CUDA
- tensorflow
- librosa

# References

> Be sure to always include important sources here!

## Librosa Documentation and Tutorials
- https://www.youtube.com/watch?v=MhOdbtPhbLU
- https://librosa.org/doc/latest/index.html

## Papers and Articles on Audio Data Handling
- https://hackernoon.com/audio-handling-basics-how-to-process-audio-files-using-python-cli-jo283u3y
- https://arxiv.org/pdf/1905.00078.pdf
- https://www.kdnuggets.com/2020/02/audio-data-analysis-deep-learning-python-part-1.html

## KNOWN ISSUE (Ale only System)
- Must run music_analyzer in sudo without virtualenv due to permission problems
  > ghoul@Tokyo:~/Documents/GitHub/MusicAnalysis/Music_Analysis$ sudo python3 music_parser.py
  
- Chord Droid isn't up to date with Gradle
> Change instruction from their ReadMe to:

> In dependencies (gradle for the module):
`api fileTree(dir: 'libs', include: ['*.aar'])`

> In repopsitories (gradle for the module): 

`repositories{
    flatDir {
        dirs 'libs'
    }
}`
