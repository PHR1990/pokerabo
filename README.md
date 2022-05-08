<img src="https://github.com/PHR1990/pokerabo/blob/main/gitbanner.png" 
     style="width: 500px; height: auto;"/>


# Pokerabo
This is a project to be used for rabobank candidates. (and an experiment for us to see how to better interview candidates)

## Installation
### Backend
The backend only requires maven (and JDK), but it is also recommended to use a lombock plugin

Within the backend folder:
```bash
mvn clean install
mvn spring-boot:run
```
### Frontend
The frontend requires Node.js

Wihtin the frontend folder:
```bash
npm install -g @angular/cli
npm install
ng serve
```

## Project Description

The project is a simulation of a pokemon battle between two pokemons.

A user would choose between 2 pokemons and their levels and press next turn to simulate turn by turn.

The backend will read pokemon data from an external API (https://pokeapi.co/) and build the battle information. 
gathered information from the external API must be stored to avoid calling them unecessary (to be polite using their public API).

When turns are completed the backend will maintain the state of the battle (only a single user is supported) and the backend sends the instructions to be
processed by the frontend to display things in order (texts, damage updates, possibly animations, etc)

## Assignment high level goals

- Improve the code quality
  - Add unit tests
  - Decouple logic
  - Separate classes into the proper packages
  - Review comments on spots (fixing every single thing is surely impossible in an appropriate time frame)
- Implement features 
  - Frontend must display texts properly (broken into new lines)
  - Block a user from submitting several (Next turn) requests
  - Backend must save in a local database when an already known resource (Pokemon or Move) is queried
  - Calculations must be improved:
    - Apply STAB damage
    - Account for Weaknesses and Resistances
  - Support New Move Types
    - Stat modifiers (must be added to the calculations)
    - Accuracy modifiers (must be added to the calculations)
