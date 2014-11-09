#!/usr/bin/env python

import os
import csv
import sys

import struct

def convert(in_file, out_file):
   with open(in_file, 'r') as in_file_handle:
      with open(out_file, 'w+') as out_file_handle:
         inreader = csv.reader(in_file_handle)
         outwriter = csv.writer(out_file_handle)
         for color, name in inreader:
            if len(name) == 0:
               continue
            r,g,b = hex_to_rgb(color)
            outwriter.writerow([name, r, g, b])
         
            
def hex_to_rgb(value):
   value = value.lstrip('#')
   lv = len(value)
   return tuple(int(value[i:i+lv/3], 16) for i in range(0, lv, lv/3))

if __name__ == '__main__':
   in_filename = sys.argv[1]
   out_filename = sys.argv[2]
   convert(in_filename, out_filename)

