Echo - A Voice Recognition Chat App with AI Services Integration 🗣️🤖
======================================================================

Echo is a modern and intuitive chat application that leverages the power of voice recognition and the ChatGPT API to provide a seamless and engaging chat experience. The main concept of Echo is to support various AI services through the app in the future, making it an all-in-one AI-powered tool 🌐. With future improvements and updates, the aim is to deliver even better user experiences.

Table of Contents 📚
--------------------

*   [Features](#features)
*   [Technologies](#technologies)
*   [Installation](#installation)
*   [Usage](#usage)
*   [Testing](#testing)
*   [Future Plans](#future-plans)
*   [Contributing](#contributing)
*   [License](#license)

Features 🎉
-----------

*   Voice recognition for hands-free chatting 🎙️
*   ChatGPT API integration for engaging and intelligent conversations 💬
*   Clean and simple user interface 📱
*   High-performance design ⚡

Technologies 💻
---------------

* Android + Kotlin: The entire codebase is written in Kotlin, a modern and expressive programming language, while Echo is built on the Android platform.
* Jetpack + Compose: The app utilizes Android Jetpack libraries to ensure best practices and takes advantage of the latest Android features, while the user interface is built using Jetpack Compose, a modern UI toolkit for Android.
* MVVM pattern: Echo is designed using the Model-View-ViewModel (MVVM) architecture pattern for a modular and maintainable codebase.
* Coroutines + Retrofit: The app leverages Kotlin coroutines for efficient and non-blocking asynchronous operations and uses the Retrofit library for making API calls to the endpoint API, ensuring a seamless integration with the backend services.

Installation 🔧
---------------

Clone the repository:

```
git clone https://github.com/dongyi-chun/echo.git
```

Open the project in your preferred Android development environment, such as Android Studio, and build the project.

Usage 📱
--------

1.  Launch the Echo app on your Android device or emulator.
2.  Grant the necessary permissions for the app to access your device's microphone.
3.  Press the "Start Listening" button and speak your message.
4.  The app will convert your speech to text and communicate with the ChatGPT API to generate a response.
5.  Enjoy the conversation!

Testing 🧪
----------

Echo is designed with testability in mind. Unit tests and integration tests can be run directly from your development environment or by using the following command in the terminal:

```
./gradlew test connectedCheck
```

Future Plans 🚀
---------------

* Improve the voice recognition experience.
* Integrate additional language support for a more inclusive chat experience.
* Implement user authentication for personalized conversations.
* Enhance the user interface with additional customization options.
* Support other OpenAI APIs for a more comprehensive AI experience.
* Integrate image AI features for advanced image analysis and manipulation 🖼️.
* Incorporate video AI features for real-time video processing and analysis 🎥.
* Explore other relevant AI features to expand the app's capabilities.


Contributing
------------

Welcome contributions from the community! If you have any ideas for improvements or new features, please open an issue or submit a pull request.

License
-------

Echo is released under the [MIT License](LICENSE).