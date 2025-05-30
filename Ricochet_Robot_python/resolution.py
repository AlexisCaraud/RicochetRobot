import copy
import matplotlib.pyplot as plt
import numpy as np
import time
from random import *
from plateau import *

class Algo:
    def __init__(self, plateau):
        self.plateau = plateau
        self.list_mem = []
        self.list_etat =[]
        self.best_sol = []

    def reset_mem(self):
        self.list_mem = []
        self.list_etat =[]
        self.best_sol = []

    def solution_fast(self, couleur, arret, optim=False):  # solution la plus rapide: on test la solution unique après chaque déplacement
        start = time.perf_counter()
        self.plateau.replacer_robots(self.plateau.init)
        (a, b) = self.plateau.objectif.coord()
        tab_dis = self.plateau.tableau_distance(a, b, couleur)
        L = self.plateau.get_coord_robots()
        list_mem = [L]      # liste des états rencontrés pour éviter les redondances
        list_etat = [[L]]   # liste des états par nombre de coup effectués pour effectuer l'opération n+1
        list_coup = [[]]      # liste des coups effectués (robot et direction)
        compteur = 0        # initialisation du compteur
        robots = copy.deepcopy(ROBOT)
        if not optim:
            robots.remove(couleur)
        DIR=['haut', 'droite', 'bas', 'gauche']     # liste des déplacements
        (x, y) = L[couleur]
        if tab_dis[x][y] != [[], []] and self.plateau.tableau[a][b].robot.couleur == None:   #si on sait comment arriver à l'objectif et que personne n'est dessus
                end = time.perf_counter()
                return [tab_dis[x][y][0], tab_dis[x][y][1], end-start]
        while compteur < arret:       # tant que le robot principal n'est pas à 1 déplacement de l'objectif
            
            list_etat.append([])                    # on débute l'opération n+1
            if compteur != 0:
                list_coup.append([])
            for k in range(len(list_etat[compteur])):   # on parcourt les états précédents
                for dir in DIR:                             # on parcourt les directions
                    for col in robots:                         # on parcourt les robots
                        self.plateau.replacer_robots(list_etat[compteur][k])    # on prend le tableau correspondant à un état déjà visité
                        self.plateau.dep(col, dir)                    # on effectue 1 déplacement de plus
                        test = self.plateau.get_coord_robots()
                        if test not in list_mem:
                            list_mem.append(test)                 # on ajoute l'état x à la liste
                            list_etat[compteur + 1].append(test)  # on ajoute l'état x à la liste
                            if compteur == 0:
                                list_coup[0].append([col, dir])
                            else:
                                list_coup[compteur].append(list_coup[compteur-1][k]+[col, dir])
                            tab_dis = self.plateau.tableau_distance(a, b, couleur)
                            (x, y) = test[couleur]
                            if tab_dis[x][y] != [[], []] and self.plateau.tableau[a][b].robot.couleur == None:
                                end = time.perf_counter()
                                return [compteur + 1 + tab_dis[x][y][0], list_coup[compteur][-1] + tab_dis[x][y][1], end-start]
            compteur += 1
        

    def branch_and_bound(self, couleur, sol, dep_secondaire, optim = False): # solution optimale en partant d'une solution rapide sol qui utilise dep_secondaire de fois les robots secondaires
        self.reset_mem()
        self.best_sol = sol
        self.plateau.replacer_robots(self.plateau.init)
        (a, b) = self.plateau.objectif.coord()
        L = self.plateau.get_coord_robots()
        (x, y) = L[couleur]
        distance = self.plateau.tableau_distance_min(a, b)[x][y]    #distance minimale séparant robot et objectif
        born_inf = distance + dep_secondaire        #distance + dep_secondaire est une borne inférieure du nombre minimum de coups
        born_sup = self.best_sol[0]      #le nombre de déplacement de la solution initiale est une borne supérieure du problème
        print(distance, dep_secondaire, born_sup)
        while born_inf < born_sup:     
            print(born_sup-born_inf)
            dep_secondaire += 1
            self.best_sol = self.solution_optim_n_coups(couleur, dep_secondaire, optim)
            self.plateau.replacer_robots(self.plateau.init)
            L = self.plateau.get_coord_robots()
            (x, y) = L[couleur]
            distance = self.plateau.tableau_distance_min(a, b)[x][y]    #distance minimale séparant robot et objectif
            born_inf = distance + dep_secondaire        #distance + dep_secondaire est une borne inférieure du nombre minimum de coups
            born_sup = self.best_sol[0]      #le nombre de déplacement de la meilleure solution est une borne supérieure du problème
        print("fini")
        return self.best_sol
    
    def solution_optim_n_coups(self, couleur, n, optim):      #renvoie la solution optimale en au plus n coups secondaires, optim dit si le robot principal compte dans les deplacements secondaires
        start = time.perf_counter()
        (a, b) = self.plateau.objectif.coord()
        tab_dis = self.plateau.tableau_distance(a, b, couleur)
        L = self.plateau.get_coord_robots()
        if self.list_mem == []:
            self.list_mem = [L]      # liste des états rencontrés pour éviter les redondances
            etat = Etat(self.plateau, self.plateau.get_coord_robots(), [])
            self.list_etat = [[etat]]   # liste des états par nombre de coup effectués pour effectuer l'opération n+1
        compteur = len(self.list_etat) - 1        # initialisation du compteur
        robots = copy.deepcopy(ROBOT)
        if not optim:
            robots.remove(str(couleur))        # accélère l'algo mais on peut rater la solution optimale
        DIR=['haut', 'droite', 'bas', 'gauche']     # liste des déplacements
        (x, y) = L[couleur]
        while compteur < n:       
            self.list_etat.append([])                    # on débute l'opération n+1
            for etat in self.list_etat[-2]:   # on parcourt les états précédents
                for dir in DIR:                             # on parcourt les directions
                    for col in robots:                         # on parcourt les robots
                        self.plateau.replacer_robots(etat.coord)    # on prend le plateau correspondant à un état déjà visité
                        self.plateau.dep(col, dir)                    # on effectue 1 déplacement de plus
                        test = self.plateau.get_coord_robots()
                        if test not in self.list_mem:                    # si c'est un nouvel état:
                            self.list_mem.append(test)                 # on ajoute l'état à la liste mémoire
                            new_etat = etat.changer_etat([col, dir])
                            self.list_etat[-1].append(new_etat)  # on ajoute l'état à la liste des états rang compteur+1 
                            tab_dis = self.plateau.tableau_distance(a, b, couleur)
                            (x, y) = test[couleur]      #coordonnées du robot principal
                            if tab_dis[x][y] != [[], []] and self.plateau.tableau[a][b].robot.couleur == None and self.best_sol[0] > tab_dis[x][y][0] + compteur + 1:
                                self.best_sol = [compteur + 1 + tab_dis[x][y][0], new_etat.list_coup + tab_dis[x][y][1]]
            compteur += 1
        end = time.perf_counter()
        return self.best_sol + [end - start]
        
    # def nombre_dep_sec(self, sol): # renvoie le nombre de déplacements secondaires dans sol
    #     liste = sol[1] # liste des déplacements de la solution
    #     nombre_dep = 0
    #     for dep in liste:
    #         if dep != 'haut' and dep != 'bas' and dep != 'droite' and dep != 'gauche':
    #             nombre_dep += 1
    #     return nombre_dep
    
    def nombre_dep_sec(self, sol): # renvoie le nombre de déplacements secondaires dans sol
        liste = sol[1] # liste des déplacements de la solution
        col = liste[-2]
        nombre_dep = int(len(liste) / 2)
        for i in range(len(liste) - 2, -1, -2):
            if (liste[i] == col):
                nombre_dep -= 1
            else:
                break
        return nombre_dep
    
class Etat:
    def __init__(self, plateau, coord=None, list_coup=None):
        self.plateau = plateau
        self.coord = coord     # dictionnaire des coordonnées de chaque robot
        self.list_coup = list_coup  #liste de coups pour arriver dans cet état du plateau

    def changer_etat(self, coup):
        self.plateau.replacer_robots(self.coord)
        self.plateau.dep(coup[0], coup[1])
        coord = self.plateau.get_coord_robots()
        list_coup = self.list_coup + coup
        return Etat(self.plateau, coord, list_coup)

    def __repr__(self):
        return 'coord:{}, list_coups:{}'.format(self.coord, self.list_coup)

if __name__=='__main__':
    P = Plateau(16,10)
    P.random()
    P.open('Map2')
    algo = Algo(P)
    P.affich()
    col = P.objectif.couleur
    init = algo.solution_fast(col, np.inf)