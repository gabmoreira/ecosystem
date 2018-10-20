# ecosystem

This project consists of a simulation of preys and predators who interact with eachother and with plants in the environment. Each animal has certain needs and may die of either famine, get killed or reach a random dying age. The predators may hunt the preys if they're in their field of vision and all animals try to reproduce if they verify the conditions to do so.
The simulation ends after a finite number of iterations.

## Simulation
The simulation runs on a TCP server and accepts a maximum of 4 clients (which correspond to 4 adjacent views of the complete environment).

## Server
The binary files are provided in the [/bin](https://github.com/gabmoreira/ecosystem/tree/master/project/bin) folder. To run the server navigate to this folder and tap on your terminal: 
```
java main.ServerMain
```

## Clients
The clients can connect to the server by running the file GraphicalDisplay in [/bin](https://github.com/gabmoreira/ecosystem/tree/master/project/bin) on a separate terminal. This opens a new simulation window that is connected to the already existing ones.
```
java main.GraphicalDisplay
```
## Author
* Gabriel Moreira

