(ns bouncing-ball)

(import 
 '(java.awt Color Graphics Dimension)
 '(java.awt.image BufferedImage)
 '(javax.swing JPanel JFrame))

(def xdim 100)
(def ydim 100)

(def ball-radius 5)

(def num-balls 6)

(defstruct World :balls :ball-locs :bound)
(defstruct Ball :mass :velocity :elasticity)
(defstruct Location :x :y)

(defn create-balls [n]
  "Create n randomised balls each wrapped in a ref"
  (for [x (range n)]
    (ref (struct-map Ball 
                 :mass (rand)
                 :velocity [(rand) (rand)]
                 :elasticity (rand)))))
  
(defn init-world []
  "Create a world with a number of randomised balls at randomised locations"
  (let [balls (create-balls num-balls)
        bound {:x xdim :y ydim}]
    (struct-map World
            :balls balls
            :ball-locs (map (fn [_] (ref {:x (rand-int (bound :x)) :y (rand-int (bound :y))})) balls)
            :bound bound))) 
            
(def world (init-world))


(defn render-ball [g ball loc]
  (doto g
      (.setColor (. Color blue))
      (.drawOval (- (loc :x) ball-radius) 
                 (- (loc :y) ball-radius)
                 (* 2 ball-radius) (* 2 ball-radius))))
  
(defn render [g]
  (let [img (new BufferedImage (-> world :bound :x) (-> world :bound :y) 
                 (. BufferedImage TYPE_INT_ARGB))
        bg (. img (getGraphics))]
    (doto bg
      (.setColor (. Color white))
      (.fillRect 0 0 (. img (getWidth)) (. img (getHeight))))
    (doall
      (map (fn [ball loc]
             (render-ball bg ball loc)) (world :balls) (world :ball-locs)))
    (. g (drawImage img 0 0 nil))
    (. bg (dispose))))

(def panel (doto (proxy [JPanel] []
                        (paint [g] (render g)))
             (.setPreferredSize (new Dimension 
                                    xdim 
                                    ydim))))

(def frame (doto (new JFrame) (.add panel) .pack .show))