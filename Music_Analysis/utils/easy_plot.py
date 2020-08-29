import matplotlib.pyplot as plt

def plot_fn(data=tuple(), labels=('X','Y'), scale="linear", title="Graph"):
    '''Disclaimer: Streamlined plotting from matplotlib functions,
                    for ease of use not claiming ownership. '''
    x, y = data
    x_label, y_label = labels
    plt.plot(x,y)
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.yscale(scale)
    plt.title(title)
    plt.show()
    plt.savefig(fname=title.join('.png'), format='png')
    return