%module UMLRecognizer
%include <std_string.i>

%pragma(java) jniclassimports="import java.util.ArrayList;"
%pragma(java) jniclasscode=%{
    static {
	    try {
	    	ArrayList<String> list = new ArrayList<String>();
	    	
	    	
	    	list.add("liblept168"); //$NON-NLS-1$
	    	list.add("libtesseract302"); //$NON-NLS-1$
	    	list.add("opencv_core242"); //$NON-NLS-1$
	    	list.add("opencv_highgui242"); //$NON-NLS-1$
	    	list.add("opencv_imgproc242"); //$NON-NLS-1$
	  
	    	list.add("UMLrecogn"); //$NON-NLS-1$


	    	/**
	    	 * Iterate through the list of libraries and remove all libraries from the list
	    	 * that could be loaded. Repeat this step until all libraries are loaded.
	    	 * The Reason: You can only load a library, if all dependent libraries have
	    	 * already been loaded. So you need to know the correct ordering from the
	    	 * leafs to the root. Load the leafs first!
	    	 */
	    	ArrayList<String> toRemove;
	    	int i = 10; //cancel after 10 iterations. You probably forgot to mention a library to load.
	    	while (!list.isEmpty() && (i > 0)) {
	    		toRemove = new ArrayList<String>();
	    		int j = 0;
	    		while (j <= list.size() - 1) {
	    			try {
	    				System.loadLibrary(list.get(j));
	    				toRemove.add(list.get(j));
	    				System.out.println("loaded library " + j + ": " + list.get(j)); //$NON-NLS-1$
	    			} catch (Throwable e) {
	    				System.out.println(e.getMessage());
	    				System.out.println("error loading lib: " + list.get(j)); //$NON-NLS-1$
	    			}
	    			j++;
	    		}
	    		for (String lib : toRemove) {
	    			System.out.println("removing lib : " + lib); //$NON-NLS-1$
	    			list.remove(lib);
	    		}
	    		i--;
	    	}
	    } catch (UnsatisfiedLinkError e) {
	      System.err.println("Native code library failed to load. \n" + e); //$NON-NLS-1$
	      throw e;
	    }
    }
%}

%{
#include <vector>
#include <stdexcept>
%}

%include <std_common.i>
%include <stdint.i>
%include <std_except.i>

namespace std {

    template<class T> class vector {
      public:
        typedef size_t size_type;
        typedef T value_type;
        typedef const value_type& const_reference;
        vector();
        vector(size_type n);
        vector(const vector& o);
		// Redefine to 32-bit integer (Java constraint)
		// size_type size() const;
        size_type capacity() const;
        void reserve(size_type n);
        %rename(isEmpty) empty;
        bool empty() const;
        void clear();
        void push_back(const value_type& x);
        %extend {
            const_reference get(int i) const throw (std::out_of_range) {
                int size = int(self->size());
                if (i>=0 && i<size)
                    return (*self)[i];
                else
                    throw std::out_of_range("vector index out of range");
            }
            value_type set(int i, const value_type& VECTOR_VALUE_IN) throw (std::out_of_range) {
                int size = int(self->size());
                if (i>=0 && i<size) {
					const T old = $self->at(i);
					(*self)[i] = VECTOR_VALUE_IN;
					return old;
				} else {
					throw std::out_of_range("vector index out of range");
				}
            }
            int32_t size() const {
              return $self->size();
            }
            void removeRange(int32_t from, int32_t to) {
              $self->erase($self->begin()+from, $self->begin()+to);
            }
        }
    };
}

// Java typemaps for autoboxing in return types of generics
%define AUTOBOX(CTYPE, JTYPE)
%typemap(autobox) CTYPE, const CTYPE&, CTYPE& "JTYPE"
%enddef
AUTOBOX(double, Double)
AUTOBOX(float, Float)
AUTOBOX(boolean, Boolean)
AUTOBOX(signed char, Byte)
AUTOBOX(short, Short)
AUTOBOX(int, Integer)
AUTOBOX(long, Long)
AUTOBOX(SWIGTYPE, $typemap(jstype,$1_basetype))

%typemap(javabase) std::vector "java.util.AbstractList<$typemap(autobox,$1_basetype::value_type)>"
%typemap(javainterfaces) std::vector "java.util.RandomAccess"
%typemap(jstype) std::vector get "$typemap(autobox,$1_basetype)"
%typemap(jstype) std::vector set "$typemap(autobox,$1_basetype)"
%typemap(jstype) std::vector &VECTOR_VALUE_IN "$typemap(autobox,$1_basetype)"
%typemap(javacode) std::vector %{
  $javaclassname(java.util.Collection<$typemap(autobox,$1_basetype::value_type)> e) {
    this.reserve(e.size());
    for($typemap(autobox,$1_basetype::value_type) value: e) {
      this.push_back(value);
    }
  }
%}

%template(StringList) std::vector<std::string>;

class UMLrecog
{
public:
	UMLrecog();
	~UMLrecog();
	void clear();
  void setParameters( bool isUseCase=false, bool showImages=false, int thresh=230, double sizeRate=1.0, double distNeightborObjects=20, double coverAreaThr=1.0 );
  bool setImage( std::string fileName );
  void setTessPath( std::string tessPath );
	int process();
  std::vector<std::string> getXMIcontent();
};