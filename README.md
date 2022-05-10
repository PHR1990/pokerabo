<img src="https://github.com/PHR1990/pokerabo/blob/main/gitbanner.png" 
     style="width: 500px; height: auto;"/>


# Pokerabo
This is a project to be used for rabobank candidates. (and an experiment for us to see how to better interview candidates)

## Installation
### Backend
The backend requires maven, jdk (at least 15) and lombok. Notice that the project is configured for java 17, but it was also tested on java 15

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

## Assignment Requirements
### Backend
- Refactor
  - Make sure that no existing business logic is broken (define a class for this)
  - Adding unit tests might be an idea to help during this process
  - Make it more readable
  - Decouple when possible/cleaner
  - Note: We are aware that not everything can be refactored in this timeframe, so, refactoring + commenting other subjects to it is a good idea.
- Code review (Define ONE class to be reviewed)
  - Code smells, confusing code, improper ways, etc.
- Implement features
   - Fix the broken unit tests (Smoke tests SHOULD NOT be broken when this project is running properly the first time)
   - Calculations must be improved:
    - Apply STAB damage
    - -- note to selv. This is a single item, re-write to make it a single item
### Frontend
- Unit test
  - Component 'simulate' can be tested with any strategy/framework you feel is relevant.
- Implement features  
  - Some messages are displayed leaking outside of the screen. Find a way to solve that

## What is evaluated
 - Your thought process 
 - How clean/readable is your code 
 - Your strenghts (show us what you are best at)
 - Note: These points will be discussed in the technical interview 

## What is NOT the goal
 - Ruin your weekend with a long assignment
 - Make you spend way too much time (over 3 hours) on this.
 - This is not supposed to be a stressful assignment. We want this to be a lighthearted assignment to be used as input for the technical interview.
