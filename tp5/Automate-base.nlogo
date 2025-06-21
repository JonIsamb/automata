__includes [ "automate.nls" ]

globals [
  liste-etats
  liste-transitions
  nb-points
  nb-objects-in-box
  game-over  ; Add game state flag
]

turtles-own[
  current-state
  next-state
  stack
  button-G
  button-A
  button-N
  button-E
  button-S
  button-W
  button-J
  button-P
  button-D
  button-Q
  objet-tenu
  last-y
]

to setup
  clear-all
  set nb-points 0
  set nb-objects-in-box 0
  set game-over false  ; Initialize game state
  setup-player
  setup-objects
  reset-ticks
end

to setup-player
  create-turtles 1 [
    set size 4
    set shape "player"
    setxy random-xcor random-ycor
    set current-state "player-stop"
    set next-state "player-stop"
    set stack []
    set stack lput "Z" stack
    set button-G false
    set button-A false
    set button-N false
    set button-E false
    set button-S false
    set button-W false
    set button-J false
    set button-P false
    set button-D false
    set button-Q false
    set last-y ycor
    set objet-tenu 0
  ]
end

to setup-objects
  ask n-of 5 patches with [(pcolor = black) and (any? turtles-here = false)] [
    sprout 1  [
      set shape "target"
      set size 1
      set color yellow
    ]
  ]
  ask one-of patches with [(pcolor = black) and (any? turtles-here = false)] [
    sprout 1  [
      set shape "box"
      set size 3
      set color red
    ]
  ]
  ask n-of 3 patches with [(pcolor = black) and (any? turtles-here = false)] [
    sprout 1  [
      set shape "star"
      set size 1
      set color yellow
    ]
  ]
end

to go
  if game-over [ stop ]  ; Stop simulation when game over
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    process-automaton
    reset-buttons
  ]
  tick
end

to process-automaton
  ;; Traitement de l'automate selon l'état actuel
  if current-state = "stop" [ process-player-stop ]
  if current-state = "move" [ process-move ]
  if current-state = "heads_north" [ process-heads-north ]
  if current-state = "heads_east" [ process-heads-east ]
  if current-state = "heads_south" [ process-heads-south ]
  if current-state = "heads_west" [ process-heads-west ]
  if current-state = "jump" [ process-do-jump ]
  if current-state = "pick_up" [ process-pickup ]
  if current-state = "drop" [ process-drop ]
  if current-state = "quit" [ process-quit ]

  set current-state next-state
end

to reset-buttons
  set button-G false
  set button-A false
  set button-N false
  set button-E false
  set button-S false
  set button-W false
  set button-J false
  set button-P false
  set button-D false
  set button-Q false
end

to process-player-stop
  if button-Q [
    transition-to "quit" "Q" []
  ]
  if button-J [
    transition-to "do-jump" "J" []
  ]
  if button-P and object-nearby? and objet-tenu < 10 [
    transition-to "pick_up" "P" ["O"]
  ]
  if button-D and objet-tenu > 0 [
    transition-to "drop" "D" []
  ]
  if button-G [
    transition-to "move" "G" []
  ]
  if button-N [
    transition-to "heads_north" "N" ["A"]
  ]
  if button-E [
    transition-to "heads_east" "E" ["A"]
  ]
  if button-S [
    transition-to "heads_south" "S" ["A"]
  ]
  if button-W [
    transition-to "heads_west" "W" ["A"]
  ]
  ; Removed all player-stop procedure calls
end

to process-move
  ;; Transitions depuis l'état move
  if button-Q [
    transition-to "quit" "Q" []
    stop
  ]
  if button-A [
    transition-to "stop" "A" []
    stop
  ]
  if button-N [
    transition-to "heads_north" "N" ["M"]
    stop
  ]
  if button-E [
    transition-to "heads_east" "E" ["M"]
    stop
  ]
  if button-S [
    transition-to "heads_south" "S" ["M"]
    stop
  ]
  if button-W [
    transition-to "heads_west" "W" ["M"]
    stop
  ]

  ;; Mouvement automatique vers l'avant
  forward 1
end

to process-do-jump
  let old-y ycor
  set ycor old-y + 2  ; Move up for jump

  ;; Check if we're on a star at jump peak
  if any? turtles-here with [shape = "star"] [
    ask one-of turtles-here with [shape = "star"] [ die ]
    set nb-points nb-points + 10
  ]

  set ycor old-y  ; Return to original position
  transition-to "player-stop" "ε" []
end

to process-pickup
  ;; Get adjacent patches
  let adjacent-patches [neighbors4] of patch-here

  ;; Check for objects on adjacent patches
  let targets turtles-on adjacent-patches with [shape = "target"]

  if any? targets and objet-tenu < 10 [
    ask one-of targets [ die ]
    set objet-tenu objet-tenu + 1
    set shape "player-with-star"
  ]

  transition-to "player-stop" "ε" []
end

to process-drop  ; Fixed object drop
  if objet-tenu > 0 [
    set objet-tenu objet-tenu - 1
    if any? turtles-here with [shape = "box"] [
      set nb-points nb-points + 5
      set nb-objects-in-box nb-objects-in-box + 1
      push-stack "P5"
    ]
    if objet-tenu = 0 [ set shape "player" ]
  ]
  transition-to "player-stop" "ε" []
end

to process-quit  ; Handle quit properly
  set game-over true
end

to process-heads-north
  set heading 0
  let top-symbol get-stack-top
  if top-symbol = "A" [
    pop-stack
    transition-to "stop" "ε" []
  ]
  if top-symbol = "M" [
    pop-stack
    transition-to "move" "ε" []
  ]
end

to process-heads-east
  set heading 90
  let top-symbol get-stack-top
  if top-symbol = "A" [
    pop-stack
    transition-to "stop" "ε" []
  ]
  if top-symbol = "M" [
    pop-stack
    transition-to "move" "ε" []
  ]
end

to process-heads-south
  set heading 180
  let top-symbol get-stack-top
  if top-symbol = "A" [
    pop-stack
    transition-to "stop" "ε" []
  ]
  if top-symbol = "M" [
    pop-stack
    transition-to "move" "ε" []
  ]
end

to process-heads-west
  set heading 270
  let top-symbol get-stack-top
  if top-symbol = "A" [
    pop-stack
    transition-to "stop" "ε" []
  ]
  if top-symbol = "M" [
    pop-stack
    transition-to "move" "ε" []
  ]
end

to-report object-nearby?
  ;; Check adjacent patches for objects
  let adjacent-patches [neighbors4] of patch-here
  report any? (turtles-on adjacent-patches) with [shape = "target"]
end

;; Fonctions utilitaires pour la pile
to push-stack [symbol]
  set stack lput symbol stack
end

to pop-stack
  if length stack > 1 [ ;; Garder au moins Z dans la pile
    set stack but-last stack
  ]
end

to-report get-stack-top
  if length stack > 0 [
    report last stack
  ]
  report "Z"
end

to transition-to [new-state symbol stack-ops]
  set next-state new-state
  ;; Appliquer les opérations sur la pile
  foreach stack-ops [ op ->
    push-stack op
  ]
end


; Add key handling
to __key-down [ key ]
  if key = "G" [ press-avancer ]
  if key = "A" [ press-player-stop ]
  if key = "N" [ press-north ]
  if key = "E" [ press-east ]
  if key = "S" [ press-south ]
  if key = "W" [ press-west ]
  if key = "J" [ press-do-jump ]
  if key = "P" [ press-pickup ]
  if key = "D" [ press-drop ]
  if key = "Q" [ press-quit ]
end

;; Fonctions pour les boutons de l'interface
to press-avancer
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-G true
  ]
end

to press-player-stop
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-A true
  ]
end

to press-north
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-N true
  ]
end

to press-east
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-E true
  ]
end

to press-south
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-S true
  ]
end

to press-west
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-W true
  ]
end

to press-do-jump
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-J true
  ]
end

to press-pickup
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-P true
  ]
end

to press-drop
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-D true
  ]
end

to press-quit
  ask turtles with [shape = "player" or shape = "player-with-star"] [
    set button-Q true
  ]
end

@#$#@#$#@
GRAPHICS-WINDOW
341
10
778
448
-1
-1
13.0
1
10
1
1
1
0
0
0
1
-16
16
-16
16
0
0
1
ticks
30.0

BUTTON
11
43
74
76
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
10
10
76
43
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
5
116
85
149
avancer
set bouton-avancer true\nset bouton-stop false
NIL
1
T
TURTLE
NIL
A
NIL
NIL
1

BUTTON
93
116
156
149
player-stop
set bouton-stop true\nset bouton-avancer false
NIL
1
T
TURTLE
NIL
S
NIL
NIL
1

BUTTON
1
181
78
214
gauche
set heading 270
NIL
1
T
TURTLE
NIL
G
NIL
NIL
1

BUTTON
165
182
234
215
droite
set heading 90
NIL
1
T
TURTLE
NIL
D
NIL
NIL
1

BUTTON
165
117
236
150
sauter
set bouton-sauter true
NIL
1
T
TURTLE
NIL
J
NIL
NIL
0

BUTTON
4
307
93
340
ramasser
set bouton-ramasser true
NIL
1
T
TURTLE
NIL
R
NIL
NIL
1

BUTTON
94
162
157
195
haut
set heading 0
NIL
1
T
TURTLE
NIL
U
NIL
NIL
1

BUTTON
92
222
155
255
bas
set heading 180
NIL
1
T
TURTLE
NIL
B
NIL
NIL
1

BUTTON
9
351
91
384
deposer
set bouton-deposer true
NIL
1
T
TURTLE
NIL
D
NIL
NIL
1

MONITOR
223
278
280
323
Points
nb-points
17
1
11

MONITOR
208
370
343
415
Objets dans la boîte
nb-objects-in-box
17
1
11

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cloud
false
0
Circle -7500403 true true 13 118 94
Circle -7500403 true true 86 101 127
Circle -7500403 true true 51 51 108
Circle -7500403 true true 118 43 95
Circle -7500403 true true 158 68 134

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

ghost
false
0
Polygon -7500403 true true 30 165 13 164 -2 149 0 135 -2 119 0 105 15 75 30 75 58 104 43 119 43 134 58 134 73 134 88 104 73 44 78 14 103 -1 193 -1 223 29 208 89 208 119 238 134 253 119 240 105 238 89 240 75 255 60 270 60 283 74 300 90 298 104 298 119 300 135 285 135 285 150 268 164 238 179 208 164 208 194 238 209 253 224 268 239 268 269 238 299 178 299 148 284 103 269 58 284 43 299 58 269 103 254 148 254 193 254 163 239 118 209 88 179 73 179 58 164
Line -16777216 false 189 253 215 253
Circle -16777216 true false 102 30 30
Polygon -16777216 true false 165 105 135 105 120 120 105 105 135 75 165 75 195 105 180 120
Circle -16777216 true false 160 30 30

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

monster
false
0
Polygon -7500403 true true 75 150 90 195 210 195 225 150 255 120 255 45 180 0 120 0 45 45 45 120
Circle -16777216 true false 165 60 60
Circle -16777216 true false 75 60 60
Polygon -7500403 true true 225 150 285 195 285 285 255 300 255 210 180 165
Polygon -7500403 true true 75 150 15 195 15 285 45 300 45 210 120 165
Polygon -7500403 true true 210 210 225 285 195 285 165 165
Polygon -7500403 true true 90 210 75 285 105 285 135 165
Rectangle -7500403 true true 135 165 165 270

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

person lumberjack
false
0
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Polygon -2674135 true false 60 196 90 211 114 155 120 196 180 196 187 158 210 211 240 196 195 91 165 91 150 106 150 135 135 91 105 91
Circle -7500403 true true 110 5 80
Rectangle -7500403 true true 127 79 172 94
Polygon -6459832 true false 174 90 181 90 180 195 165 195
Polygon -13345367 true false 180 195 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285
Polygon -6459832 true false 126 90 119 90 120 195 135 195
Rectangle -6459832 true false 45 180 255 195
Polygon -16777216 true false 255 165 255 195 240 225 255 240 285 240 300 225 285 195 285 165
Line -16777216 false 135 165 165 165
Line -16777216 false 135 135 165 135
Line -16777216 false 90 135 120 135
Line -16777216 false 105 120 120 120
Line -16777216 false 180 120 195 120
Line -16777216 false 180 135 210 135
Line -16777216 false 90 150 105 165
Line -16777216 false 225 165 210 180
Line -16777216 false 75 165 90 180
Line -16777216 false 210 150 195 165
Line -16777216 false 180 105 210 180
Line -16777216 false 120 105 90 180
Line -16777216 false 150 135 150 165
Polygon -2674135 true false 100 30 104 44 189 24 185 10 173 10 166 1 138 -1 111 3 109 28

person service
false
0
Polygon -7500403 true true 180 195 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285
Polygon -1 true false 120 90 105 90 60 195 90 210 120 150 120 195 180 195 180 150 210 210 240 195 195 90 180 90 165 105 150 165 135 105 120 90
Polygon -1 true false 123 90 149 141 177 90
Rectangle -7500403 true true 123 76 176 92
Circle -7500403 true true 110 5 80
Line -13345367 false 121 90 194 90
Line -16777216 false 148 143 150 196
Rectangle -16777216 true false 116 186 182 198
Circle -1 true false 152 143 9
Circle -1 true false 152 166 9
Rectangle -16777216 true false 179 164 183 186
Polygon -2674135 true false 180 90 195 90 183 160 180 195 150 195 150 135 180 90
Polygon -2674135 true false 120 90 105 90 114 161 120 195 150 195 150 135 120 90
Polygon -2674135 true false 155 91 128 77 128 101
Rectangle -16777216 true false 118 129 141 140
Polygon -2674135 true false 145 91 172 77 172 101

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

player
false
0
Circle -7500403 true true 110 5 80
Polygon -13840069 true false 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105
Polygon -1184463 true false 105 90 195 90 180 195 120 195
Circle -1 true false 122 31 16
Circle -16777216 true false 125 36 8
Circle -1 true false 158 31 16
Circle -16777216 true false 162 36 8

player-jump
false
0
Circle -7500403 true true 110 5 80
Polygon -13840069 true false 105 89 120 194 75 209 120 254 159 241 150 224 184 238 231 229 252 217 180 194 195 89
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105
Polygon -1184463 true false 105 90 195 90 180 195 120 195
Circle -1 true false 122 31 16
Circle -16777216 true false 125 36 8
Circle -1 true false 158 31 16
Circle -16777216 true false 162 36 8

player-with-star
false
0
Circle -7500403 true true 110 5 80
Polygon -13840069 true false 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105
Polygon -1184463 true false 105 90 195 90 180 195 120 195
Circle -1 true false 122 31 16
Circle -16777216 true false 125 36 8
Circle -1 true false 158 31 16
Circle -16777216 true false 162 36 8
Polygon -1184463 true false 225 180 225 180 240 150 255 180 225 180
Polygon -1184463 true false 225 165 240 195 255 165 225 165

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

tree-2
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.4.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
