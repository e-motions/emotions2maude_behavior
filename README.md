# e-Motions Behavior to Maude transformation

This file is intended to document the transformation from e-Motions behavior model to a Maude model.


## NAC patterns

1. An operation with the name of the nac and the rule `<nac>@<rule>` and parameters
`Set Set{@Object} @Model -> Bool` is created.
2. Two equations are added:
    1. The *positive* equation with the following arguments:
      - The set of objects and action execution `Oid`s in the LHS of the rule.
      - The set of variables involved in the rule. Each variable is a Maude object.
      - The elements involved in the NAC, both objects and action executions.
    2. The *negative* equation with the following arguments:
      - A variable `OIDSET@:Set`.
      - A variable `OBJSET@:Set{@Object}`.
      - A variable `MODEL@:@Model`.
      And `false` as result. This is the `owise` equation.

# Done



## TO-DO

1. Rules information
2. MTEs
3. Module importations
