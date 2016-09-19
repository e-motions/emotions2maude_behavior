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

### Representing a e-Motions LHS object in Maude
A Maude object is represented by the operator `<_:_|_> : Oid Cid StructuralFeatures -> Object`
- **Oid**: For the `Oid` we create a variable to be matched with the object. Such variable
has the same name as the behavior object has. As for the sort, we use the `OCL-Type`.
- **Cid**: For the `Cid` we use a variable which is the class name in upper case and each package is appended using the `@` symbol. The sort of such variable is the Maude sort used for such class.
- **StructuralFeatures**: The most complex part. We distinguish if StructuralFeatures are needed for such object or not. They are needed if output links or slots are present in the object.
# Done



## TO-DO

1. Rules information
2. MTEs
3. Module importations

### little to-dos
- do not have singleton instances, they depend on the transformations.
- `EmotionsModule`: change getters for hashmap with names.
