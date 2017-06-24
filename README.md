# Intelligent Agent for Catan

This is AI agent use to modelling for negotiation in game of Settler of Catan

## Project Description

Deep neural network has been successfully proved to be an effective tool as an evaluation function. In this report, we will investigate the effect of using deep learning in a board game Settler of Catan. We use deep convolutional neural network of 12 layers with no max pooling layers. We train this deep convolutional neural network with data from computer self-play game. The deep convolutional neural network able to correctly predict the action of computer move in 43%. However, as the rule of the game that limited Settler of Catan to have sequence of hidden information, we also formed an opponent modelling to predict the hidden information in the game with hidden markor model.

## Data Gathering

We gather data of one thousand self-play game of both SmartSettler and HMM agent with corresponding to around 100,000 datasets for both training and testing with an input data format of three-dimensional matrix of size 23 by 23 by 20. We believe give extract information such as number of production from the beginning for each vertex will give neural network a richer information in order to predict a meaningful label.

We store our data in the form Hierarchical Data Format (HDF5) due to it flexibility and ease of use.

## Result

Our agent is out perform the old agent even with hidden state. Moreover, it ablet o predict the hidden state with a reasonable error rate.



