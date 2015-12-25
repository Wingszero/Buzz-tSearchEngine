import socket
import struct
from binascii import hexlify

import numpy as np
import matplotlib.pyplot as plt

caffe_root = 'caffe/'
import sys
sys.path.insert(0, caffe_root + 'python')

import caffe

plt.rcParams['figure.figsize'] = (10, 10)
plt.rcParams['image.interpolation'] = 'nearest'
plt.rcParams['image.cmap'] = 'gray'

caffe.set_device(0)
caffe.set_mode_gpu()

net = caffe.Net(caffe_root + 'models/bvlc_reference_caffenet/deploy.prototxt',
                caffe_root + 'models/bvlc_reference_caffenet/bvlc_reference_caffenet.caffemodel',
                caffe.TEST)

transformer = caffe.io.Transformer({'data' : net.blobs['data'].data.shape})
transformer.set_transpose('data', (2, 0, 1))
transformer.set_mean('data', np.load(caffe_root + "python/caffe/imagenet/ilsvrc_2012_mean.npy").mean(1).mean(1))
transformer.set_raw_scale('data', 255)
transformer.set_channel_swap('data', (2,1,0))

net.blobs['data'].reshape(50, 3, 227, 227)

def classifyImage(path):
    net.blobs['data'].data[...] = transformer.preprocess('data', caffe.io.load_image(path))
    out = net.forward()
    print("Predicted class is #{}.".format(out['prob'][0].argmax()))

    imagenet_labels_filename = caffe_root + 'data/ilsvrc12/synset_words.txt'
    labels = np.loadtxt(imagenet_labels_filename, str, delimiter='\t')

    top_k = net.blobs['prob'].data[0].flatten().argsort()[-1:-6:-1]
    print labels[top_k][0]
    return labels[top_k][0]

address = ("0.0.0.0", 8081)
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(address)
s.listen(1000)

counter = 0
print 'Start listening'
while True:
    try:
        client, addr = s.accept()
        print 'got connected from', addr

        filename = open('upload/'+str(counter)+".jpg", "w")
        buf = client.recv(4)
        print hexlify(buf)
        size = int(hexlify(buf), 16)

        print 'image size: ', str(size)

        while size > 0:
            if size > 8192:
                buf_size = 8192
            else:
                buf_size = size
            buf = client.recv(buf_size)
            size -= len(buf)
            print "received", str(len(buf))

            if not buf:
                print "in here"
            filename.write(buf)

        print 'finish receiving'
        filename.close()
        client.send(classifyImage('upload/' + str(counter) + '.jpg'))
        client.close()
    except:
        print "error"
        break
    counter += 1



