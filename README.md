# Sound Analyzer

Sound Analyzer is a Java desktop application for exploring audio and image signals in the time and frequency domains. It can visualize WAV waveforms, calculate FFT-based frequency spectra and spectrograms, play audio, and apply simple frequency-domain filters to images.

> This is a legacy educational project. The repository currently does not include a Maven or Gradle build file, automated tests, or bundled OpenCV binaries.

## Features

### Audio analysis

- Load `.wav` files from a selected directory.
- Display the audio waveform.
- Calculate and display a Fast Fourier Transform (FFT) magnitude graph.
- Generate a Short-Time Fourier Transform (STFT) spectrogram.
- Play the selected audio file.
- Show cursor-based graph values for waveform and frequency data.
- Overlay optional phoneme boundaries from matching `.phn` files.

### Image analysis

- Load `.png`, `.jpg`, and `.jpeg` images.
- Convert images to grayscale for frequency analysis.
- Display a two-dimensional frequency spectrum using OpenCV's DFT.
- Select a rectangular frequency region and reconstruct the filtered image with an inverse DFT.
- Apply a numeric threshold to the image frequency spectrum.

## Technology stack

- Java
- JavaFX and FXML for the desktop interface
- OpenCV Java bindings for DFT/IDFT operations
- Java Sound API for WAV input and playback
- AWT/Swing utilities for image rendering and file-selection support

## Requirements

- A desktop operating system with a graphical environment
- JDK 8 is recommended because it includes JavaFX and best matches the APIs used by this project
- OpenCV 3.x-compatible Java bindings and the corresponding native library
- An IDE such as IntelliJ IDEA, or an equivalent manual Java compilation setup

## Setup

Because no build configuration is committed, the simplest way to run the project is from an IDE:

1. Clone or download this repository.
2. Open the repository as a Java project.
3. Configure a JDK 8 SDK.
4. Mark `src/main/java` as the source root.
5. Mark `src/main/resources` as the resources root.
6. Add the OpenCV Java JAR to the project dependencies.
7. Add the directory containing the OpenCV native library to `java.library.path`.
8. Run the `ui.Main` class.

The launcher in `src/main/java/ui/Main.java` appends `x64` or `x86` to OpenCV's native library name. Ensure that the installed native library matches that convention, or update the loading logic for your OpenCV installation and operating system.

## Usage

1. Start the application.
2. Select **File > Open** and choose a directory containing supported files.
3. Select a `.wav`, `.png`, or `.jpg` file from the tree on the left.
4. For audio files:
   - Inspect the waveform in the upper pane.
   - Switch between **Frequency Analysis** and **Spectrogram**.
   - Select **Play** to listen to the file.
5. For image files:
   - Inspect the original image and its frequency spectrum.
   - Drag over a region of the spectrum to reconstruct the image from that region.
   - Use **Threshold** to filter frequency values numerically.

## Phoneme annotations

When an audio file has a neighboring annotation file with the same base name, the application draws its phoneme boundaries over the spectrogram.

Example:

```text
sample.wav
sample.phn
```

Each line in the `.phn` file must contain a start sample, end sample, and phoneme label:

```text
0 3050 h#
3050 4559 sh
4559 5723 iy
```

## Project structure

```text
src/main/java/
├── audio/                  WAV reading, playback, and sample utilities
├── graph/                  Waveform, FFT, and spectrogram rendering
├── ui/                     JavaFX application and spectrogram model
│   ├── controller/         Audio and image analysis controllers
│   ├── obj/                Phoneme data objects
│   └── utils/              Array, image, matrix, and UI helpers
└── META-INF/MANIFEST.MF    Application entry-point metadata

src/main/resources/
├── icons/                  Application icon
└── xml/                    JavaFX FXML layouts
```

## Current limitations

- The project has no dependency-managed build or packaging workflow.
- OpenCV loading is tied to a custom `x64`/`x86` native-library naming convention.
- File-path construction in the main controller uses a Windows separator.
- WAV decoding assumes 16-bit little-endian PCM sample data.
- Speech-to-text UI code is an unfinished placeholder and is not exposed in the main menu.
- There are no automated tests or continuous-integration checks.

## Suggested improvements

- Add Maven or Gradle configuration for reproducible builds.
- Upgrade to a current Java and JavaFX release.
- Make OpenCV native loading portable across Windows, macOS, and Linux.
- Validate and convert different WAV encodings before analysis.
- Replace deprecated thread-control calls with JavaFX tasks or executors.
- Add unit tests for sample decoding, FFT helpers, matrix conversion, and phoneme parsing.
- Add export actions for waveform, spectrum, and spectrogram images.

## Repository description and topics

Suggested GitHub description:

> JavaFX desktop app for WAV waveform, FFT and spectrogram analysis, plus OpenCV-based image frequency filtering.

Suggested GitHub topics:

```text
java
javafx
opencv
digital-signal-processing
audio-analysis
audio-visualization
waveform
fft
stft
spectrogram
image-processing
frequency-domain
speech-analysis
```

## License

No license file is currently included. Add a license before distributing or accepting external contributions.
