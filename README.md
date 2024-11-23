# SwiftBot Traffic Lights Program

## Overview

A SwiftBot (also known as Trilobot) is a Raspberry Pi powered robot used throughout the first year of Computer Science at Brunel University of London.
<!--
<img src="https://github.com/user-attachments/assets/aba33305-499e-4200-8015-221e93e61c57" alt="SwiftBot image" width="270" />
-->

### This robot featuers:
* 2x front wheel drive, 1x rear castor
* Four tactile buttons and status LEDs
* Six-zone RGB underlighting
* Front facing ultrasound distance sensor and camera mount.

<p align="center">
  <img src="https://github.com/user-attachments/assets/aba33305-499e-4200-8015-221e93e61c57" alt="SwiftBot image" width="270" />
</p>

## 🚦Project
This assignment implements a traffic light system where the robot should perform a different task based on the detected colour.

When the program finishes executing, it should also produce a log of execution in a text file.

## Additional functionality
In addition to the mimimum requirements of the assignment, I have added several additional features to this project:
* Create **profiles** on the program to store prefrences such as:
  * Username and password
  * Default colour shown during search for lights.
  * Choice of mode between “default” and “emergency".
* Different **modes**:
  * **Default mode:** SwiftBot will behave normally and adhere to the traffic lights as part of the basic requirements above.
  * **Emergency mode:** SwiftBot will avoid any traffic light unless it is green. If a traffic light is detected, it should go around it in a “C” shape.
* **Retrace steps** that are stored in a stack throughout the program.
