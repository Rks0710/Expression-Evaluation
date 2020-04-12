# Expression Evaluation: 
# This code evaluates an mathematical expression given in two forms.
    #1. It could be just stright number for example: (5 + 5) or [[(5 + 5) * 5 ] / 100] * 2.5 
    #2. It could be in variables form for example : (a[2] + a[2]) or [[a[2] + a[2]) * a[2]] / a[1]] * a[3]
      # Where these variables will be provided in a text file 
      # Text files will contain name of the array (In the example above: which is a)
      # It will also contain index number (In the example above: it would 2,1,3)
      # It will also contain the values correspoding to those indexes in that array (so (2,5), (1,100), (3, 2.5))
    #The Method to evaluate the expression recursively could be found here:
      # 
