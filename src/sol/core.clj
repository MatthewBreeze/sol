(ns sol.core
  (:require [org.clojars.cognesence.breadth-search.core :refer :all]
            [org.clojars.cognesence.matcher.core :refer :all]
            [org.clojars.cognesence.ops-search.core :refer :all]))

(def ops
  ;some platforms may be picked up
  '{pickup {:pre ( (agent ?agent)
                   (manipulable ?obj)
                   (at ?agent ?plat1 ?sub1)
                   (on ?obj ?plat1 ?sub1)
                   (holds ?agent nil)
                   )
            :add ((holds ?agent ?obj))
            :del ((on ?obj ?plat1 ?sub1)
                   (holds ?agent nil))
            :txt (pickup ?obj from ?sub)
            :cmd [grasp ?obj]
            }
    ;some platforms may be dropped
    drop    {:pre ( (at ?agent ?plat1 ?sub1)
                    (holds ?agent ?obj)
                    )
             :add ( (holds ?agent nil)
                    (on ?obj ?plat1 ?sub1))
             :del ((holds ?agent ?obj))
             :txt (drop ?obj at ?sub)
             :cmd [drop ?obj]
             }
    ;move between connected places
    move-to    {:pre ( (agent ?agent)
                       (at ?agent ?p1 ?sub1)
                       (connects ?sub1 ?sub2)
                       (section ?p1 ?sub1)
                       (section ?p1 ?sub2)
                       )
                :add ((at ?agent ?p1 ?sub2))
                :del ((at ?agent ?p1 ?sub1))
                :txt (move ?p1 to ?p2)
                :cmd [move-to ?p2]
                }

    ;climb on a climbable platform
    ;will not work from platform to platform
    climb-up {:pre ( (agent ?agent)
                     (at ?agent ?p1)
                     (climbable ?p1 ?p2)
                     )
              :add ((on ?agent ?p2))
              :del ((at ?agent ?p1))
              :txt (climb-on ?p1 to ?p2)
              :cmd [climb-up ?p2]
              }
    ;climb off a climbable platform
    ;will not work from platform to platform
    climb-down {:pre ( (agent ?agent)
                       (on ?agent ?p1)
                       (climbable ?p1 ?p2)
                       )
                :add ((at ?agent ?p2))
                :del ((on ?agent ?p1))
                :txt (climb-down ?p1 to ?p2)
                :cmd [climb-down ?p1]
                }
    ;pick up from a platform
    pick-off {:pre ((agent ?agent)
                     (at ?agent ?p1)
                     (or (and (connects ?p1 ?p2) (on ?obj ?p2)) (and (climbable ?p1 ?obj) (on ?obj ?p2)))
                     (manipulable ?obj)
                     (holds ?agent nil)
                     )
              :add ((holds ?agent ?obj))
              :del ((on ?obj ?p2))
              :txt ((pick-off ?obj to ?agent))
              :cmd [pick-off ?obj]
              }
    ;drop on a platform
    drop-on {:pre ((agent ?agent)
                    (on ?agent ?obj1)
                    (holds ?agent ?obj2)
                    (on ?obj2 nil)
                    )
             :add ((on ?obj2 ?obj1)
                    (holds ?agent nil)
                    (if (climbable ?obj2)
                      (climbable ?obj2)))
             :del ((holds ?agent ?obj2))
             :txt ((drop-on ?obj2 to ?obj1))
             :cmd [drop-on ?obj2]
             }


    climb-up-ladder {:pre ((agent ?agent)
                            (on ?agent ?p1)
                            (climbable ?p1 ?p3)
                            )
                     :add ((at ?agent ?p3))
                     :del (on ?agent ?p1)
                     :text (climb-up-ladder ?p1 to ?p3)
                     :cmd [climb-up-ladder ?p2]
                     }

    climb-down-ladder {:pre ((agent ?agent)
                              (on ?agent ?p3)
                              (climbable ?p3 ?p1)
                              )
                       :add ((at ?agent ?p1))
                       :del (on ?agent ?p3)
                       :text (climb-down-ladder ?p3 to ?p1)
                       :cmd [climb-down-ladder ?p2]
                       }

    })

(def world
  '#{(agent matty)
     (obj ladder)
     (climbable p1)
     (climbable p2)
     (climbable p3)
     (section p1 sub1)
     (section p1 sub2)
     (section p1 sub3)
     (connects p1 p2)
     (connects p2 p3)
     (connects p3 p4)
     (connects sub1 p2)
     (connects sub2 p3)
     (connects sub3 p4)
     (manipulable ladder)
     })

(def state1
  '#{(on ladder plat1 sub1)
     (at matty plat1 sub2)
     (holds matty nil)
     })