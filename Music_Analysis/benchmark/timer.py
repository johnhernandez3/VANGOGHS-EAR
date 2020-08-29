from functools import wraps
from time import time

def timer(fn):
    @wraps(fn)
    def wrap(*args, **kw):
        time_start = time()
        result = fn(*args,**kw)
        time_end = time()
        print(f'function:{fn.__name__} took:{time_end-time_start} \n args:{args} \n kwargs:{kw}')
        return result
    return wrap