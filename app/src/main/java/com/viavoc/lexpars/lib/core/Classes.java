package com.viavoc.lexpars.lib.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Classes {

    public static Map<String,List<Class>> lib;
    public static List<Packaged> annotations;
    public static Map<String,List<Class>> interfaces;

    static {
        annotations = new ArrayList<>(Arrays.asList(
                new Packaged("Deprecated", "java.lang.@Deprecated"),
                new Packaged("Override", "java.lang.@Override"),
                new Packaged("FunctionalInterface", "java.lang.@FunctionalInterface"),
                new Packaged("SafeVarargs", "java.lang.@SafeVarargs"),
                new Packaged("SuppressWarnings", "java.lang.@SuppressWarnings"),
                new Packaged("Documented", "java.lang.annotation.@Documented"),
                new Packaged("Inherited", "java.lang.annotation.@Inherited"),
                new Packaged("Native", "java.lang.annotation.@Native"),
                new Packaged("Repeatable", "java.lang.annotation.@Repeatable"),
                new Packaged("Retention", "java.lang.annotation.@Retention"),
                new Packaged("Target", "java.lang.annotation.@Target")
        ));

        interfaces = new HashMap<String, List<Class>>(){{
            put("java.lang", new ArrayList<Class>(Arrays.asList(
                    Appendable.class,
                    AutoCloseable.class,
                    CharSequence.class,
                    Cloneable.class,
                    Comparable.class,
                    Iterable.class,
                    Readable.class,
                    Runnable.class,
                    Thread.UncaughtExceptionHandler.class
            )));

            put("java.lang.annotation", new ArrayList<Class>(Arrays.asList(
                    java.lang.annotation.Annotation.class
            )));

            put("java.lang.reflect", new ArrayList<Class>(Arrays.asList(
                    java.lang.reflect.AnnotatedElement.class,
                    java.lang.reflect.GenericArrayType.class,
                    java.lang.reflect.GenericDeclaration.class,
                    java.lang.reflect.InvocationHandler.class,
                    java.lang.reflect.Member.class,
                    java.lang.reflect.ParameterizedType.class,
                    java.lang.reflect.Type.class,
                    java.lang.reflect.TypeVariable.class,
                    java.lang.reflect.WildcardType.class
            )));

            put("java.net", new ArrayList<Class>(Arrays.asList(
                    java.net.ContentHandlerFactory.class,
                    java.net.CookiePolicy.class,
                    java.net.CookieStore.class,
                    java.net.DatagramSocketImplFactory.class,
                    java.net.FileNameMap.class,
                    java.net.ProtocolFamily.class,
                    java.net.SocketImplFactory.class,
                    java.net.SocketOption.class,
                    java.net.SocketOptions.class,
                    java.net.URLStreamHandlerFactory.class
            )));

            put("java.security", new ArrayList<Class>(Arrays.asList(
                    java.security.AlgorithmConstraints.class,
                    java.security.DomainCombiner.class,
                    java.security.Guard.class,
                    java.security.Key.class,
                    java.security.KeyStore.Entry.class,
                    java.security.KeyStore.LoadStoreParameter.class,
                    java.security.KeyStore.ProtectionParameter.class,
                    java.security.Policy.Parameters.class,
                    java.security.Principal.class,
                    java.security.PrivateKey.class,
                    java.security.PrivilegedAction.class,
                    java.security.PrivilegedExceptionAction.class,
                    java.security.PublicKey.class
            )));

            put("java.security.acl", new ArrayList<Class>(Arrays.asList(
                    java.security.acl.Acl.class,
                    java.security.acl.AclEntry.class,
                    java.security.acl.Group.class,
                    java.security.acl.Owner.class,
                    java.security.acl.Permission.class
            )));

            put("java.security.cert", new ArrayList<Class>(Arrays.asList(
                    java.security.cert.CertPathBuilderResult.class,
                    java.security.cert.CertPathChecker.class,
                    java.security.cert.CertPathParameters.class,
                    java.security.cert.CertPathValidatorException.Reason.class,
                    java.security.cert.CertPathValidatorResult.class,
                    java.security.cert.CertSelector.class,
                    java.security.cert.CertStoreParameters.class,
                    java.security.cert.CRLSelector.class,
                    java.security.cert.Extension.class,
                    java.security.cert.PolicyNode.class,
                    java.security.cert.X509Extension.class
            )));

            put("java.security.interfaces", new ArrayList<Class>(Arrays.asList(
                    java.security.interfaces.DSAKey.class,
                    java.security.interfaces.DSAKeyPairGenerator.class,
                    java.security.interfaces.DSAParams.class,
                    java.security.interfaces.DSAPrivateKey.class,
                    java.security.interfaces.DSAPublicKey.class,
                    java.security.interfaces.ECKey.class,
                    java.security.interfaces.ECPrivateKey.class,
                    java.security.interfaces.ECPublicKey.class,
                    java.security.interfaces.RSAKey.class,
                    java.security.interfaces.RSAMultiPrimePrivateCrtKey.class,
                    java.security.interfaces.RSAPrivateCrtKey.class,
                    java.security.interfaces.RSAPrivateKey.class,
                    java.security.interfaces.RSAPublicKey.class
            )));

            put("java.security.spec", new ArrayList<Class>(Arrays.asList(
                    java.security.spec.AlgorithmParameterSpec.class,
                    java.security.spec.ECField.class,
                    java.security.spec.KeySpec.class
            )));

            put("java.sql", new ArrayList<Class>(Arrays.asList(
                    java.sql.Array.class,
                    java.sql.Blob.class,
                    java.sql.CallableStatement.class,
                    java.sql.Clob.class,
                    java.sql.Connection.class,
                    java.sql.DatabaseMetaData.class,
                    java.sql.Driver.class,
                    java.sql.NClob.class,
                    java.sql.ParameterMetaData.class,
                    java.sql.PreparedStatement.class,
                    java.sql.Ref.class,
                    java.sql.ResultSet.class,
                    java.sql.ResultSetMetaData.class,
                    java.sql.RowId.class,
                    java.sql.Savepoint.class,
                    java.sql.SQLData.class,
                    java.sql.SQLInput.class,
                    java.sql.SQLOutput.class,
                    java.sql.SQLXML.class,
                    java.sql.Statement.class,
                    java.sql.Struct.class,
                    java.sql.Wrapper.class
            )));

            put("java.text", new ArrayList<Class>(Arrays.asList(
                    java.text.AttributedCharacterIterator.class,
                    java.text.CharacterIterator.class
            )));

            put("java.util", new ArrayList<Class>(Arrays.asList(
                    Collection.class,
                    Comparator.class,
                    Deque.class,
                    Enumeration.class,
                    EventListener.class,
                    Formattable.class,
                    Iterator.class,
                    List.class,
                    ListIterator.class,
                    Map.class,
                    Entry.class,
                    NavigableMap.class,
                    NavigableSet.class,
                    Observer.class,
                    PrimitiveIterator.class,
                    Queue.class,
                    RandomAccess.class,
                    Set.class,
                    SortedMap.class,
                    SortedSet.class,
                    Spliterator.class,
                    Spliterator.OfDouble.class,
                    Spliterator.OfInt.class,
                    Spliterator.OfLong.class,
                    Spliterator.OfPrimitive.class
            )));

            put("java.util.concurrent", new ArrayList<Class>(Arrays.asList(
                    java.util.concurrent.BlockingDeque.class,
                    java.util.concurrent.BlockingQueue.class,
                    java.util.concurrent.Callable.class,
                    java.util.concurrent.CompletableFuture.AsynchronousCompletionTask.class,
                    java.util.concurrent.CompletionService.class,
                    java.util.concurrent.CompletionStage.class,
                    java.util.concurrent.ConcurrentMap.class,
                    java.util.concurrent.ConcurrentNavigableMap.class,
                    java.util.concurrent.Delayed.class,
                    java.util.concurrent.Executor.class,
                    java.util.concurrent.ExecutorService.class,
                    java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory.class,
                    java.util.concurrent.ForkJoinPool.ManagedBlocker.class,
                    java.util.concurrent.Future.class,
                    java.util.concurrent.RejectedExecutionHandler.class,
                    java.util.concurrent.RunnableFuture.class,
                    java.util.concurrent.RunnableScheduledFuture.class,
                    java.util.concurrent.ScheduledExecutorService.class,
                    java.util.concurrent.ScheduledFuture.class,
                    java.util.concurrent.ThreadFactory.class,
                    java.util.concurrent.TransferQueue.class
            )));

            put("java.util.concurrent.locks", new ArrayList<Class>(Arrays.asList(
                    java.util.concurrent.locks.Condition.class,
                    java.util.concurrent.locks.Lock.class,
                    java.util.concurrent.locks.ReadWriteLock.class
            )));

            put("java.util.function", new ArrayList<Class>(Arrays.asList(
                    java.util.function.BiConsumer.class,
                    java.util.function.BiFunction.class,
                    java.util.function.BinaryOperator.class,
                    java.util.function.BiPredicate.class,
                    java.util.function.BooleanSupplier.class,
                    java.util.function.Consumer.class,
                    java.util.function.DoubleBinaryOperator.class,
                    java.util.function.DoubleConsumer.class,
                    java.util.function.DoubleFunction.class,
                    java.util.function.DoublePredicate.class,
                    java.util.function.DoubleSupplier.class,
                    java.util.function.DoubleToIntFunction.class,
                    java.util.function.DoubleToLongFunction.class,
                    java.util.function.DoubleUnaryOperator.class,
                    java.util.function.Function.class,
                    java.util.function.IntBinaryOperator.class,
                    java.util.function.IntConsumer.class,
                    java.util.function.IntFunction.class,
                    java.util.function.IntPredicate.class,
                    java.util.function.IntSupplier.class,
                    java.util.function.IntToDoubleFunction.class,
                    java.util.function.IntToLongFunction.class,
                    java.util.function.IntUnaryOperator.class,
                    java.util.function.LongBinaryOperator.class,
                    java.util.function.LongConsumer.class,
                    java.util.function.LongFunction.class,
                    java.util.function.LongPredicate.class,
                    java.util.function.LongSupplier.class,
                    java.util.function.LongToDoubleFunction.class,
                    java.util.function.LongToIntFunction.class,
                    java.util.function.LongUnaryOperator.class,
                    java.util.function.ObjDoubleConsumer.class,
                    java.util.function.ObjIntConsumer.class,
                    java.util.function.ObjLongConsumer.class,
                    java.util.function.Predicate.class,
                    java.util.function.Supplier.class,
                    java.util.function.ToDoubleBiFunction.class,
                    java.util.function.ToDoubleFunction.class,
                    java.util.function.ToIntBiFunction.class,
                    java.util.function.ToIntFunction.class,
                    java.util.function.ToLongBiFunction.class,
                    java.util.function.ToLongFunction.class,
                    java.util.function.UnaryOperator.class
            )));

            put("java.util.jar", new ArrayList<Class>(Arrays.asList(
                    java.util.jar.Pack200.Packer.class,
                    java.util.jar.Pack200.Unpacker.class
            )));

            put("java.util.logging", new ArrayList<Class>(Arrays.asList(
                    java.util.logging.Filter.class,
                    java.util.logging.LoggingMXBean.class
            )));

            put("java.util.prefs", new ArrayList<Class>(Arrays.asList(
                    java.util.prefs.NodeChangeListener.class,
                    java.util.prefs.PreferenceChangeListener.class,
                    java.util.prefs.PreferencesFactory.class
            )));

            put("java.util.regex", new ArrayList<Class>(Arrays.asList(
                    java.util.regex.MatchResult.class
            )));

            put("java.util.stream", new ArrayList<Class>(Arrays.asList(
                    java.util.stream.BaseStream.class,
                    java.util.stream.Collector.class,
                    java.util.stream.DoubleStream.class,
                    java.util.stream.DoubleStream.Builder.class,
                    java.util.stream.IntStream.class,
                    java.util.stream.IntStream.Builder.class,
                    java.util.stream.LongStream.class,
                    java.util.stream.LongStream.Builder.class,
                    java.util.stream.Stream.class,
                    java.util.stream.Stream.Builder.class
            )));

            put("java.util.zip", new ArrayList<Class>(Arrays.asList(
                    java.util.zip.Checksum.class
            )));
        }};

        lib = new HashMap<String, List<Class>>(){{
            put("java.lang",
                    new ArrayList<Class>(Arrays.asList(
                            Boolean.class,
                            Byte.class,
                            Character.class,
                            Character.Subset.class,
                            Character.UnicodeBlock.class,
                            Class.class,
                            ClassLoader.class,
                            Compiler.class,
                            Double.class,
                            Enum.class,
                            Float.class,
                            InheritableThreadLocal.class,
                            Integer.class,
                            Long.class,
                            Math.class,
                            Number.class,
                            Object.class,
                            Package.class,
                            Process.class,
                            ProcessBuilder.class,
                            Runtime.class,
                            RuntimePermission.class,
                            SecurityManager.class,
                            Short.class,
                            StackTraceElement.class,
                            StrictMath.class,
                            String.class,
                            StringBuffer.class,
                            StringBuilder.class,
                            System.class,
                            Thread.class,
                            ThreadGroup.class,
                            ThreadLocal.class,
                            Throwable.class,
                            Void.class,
                            Character.UnicodeScript.class,
                            Thread.State.class,
                            ArithmeticException.class,
                            ArrayIndexOutOfBoundsException.class,
                            ArrayStoreException.class,
                            ClassCastException.class,
                            ClassNotFoundException.class,
                            CloneNotSupportedException.class,
                            EnumConstantNotPresentException.class,
                            Exception.class,
                            IllegalAccessException.class,
                            IllegalArgumentException.class,
                            IllegalMonitorStateException.class,
                            IllegalStateException.class,
                            IllegalThreadStateException.class,
                            IndexOutOfBoundsException.class,
                            InstantiationException.class,
                            InterruptedException.class,
                            NegativeArraySizeException.class,
                            NoSuchFieldException.class,
                            NoSuchMethodException.class,
                            NullPointerException.class,
                            NumberFormatException.class,
                            ReflectiveOperationException.class,
                            RuntimeException.class,
                            SecurityException.class,
                            StringIndexOutOfBoundsException.class,
                            TypeNotPresentException.class,
                            UnsupportedOperationException.class,
                            AbstractMethodError.class,
                            AssertionError.class,
                            ClassCircularityError.class,
                            ClassFormatError.class,
                            Error.class,
                            ExceptionInInitializerError.class,
                            IllegalAccessError.class,
                            IncompatibleClassChangeError.class,
                            InstantiationError.class,
                            InternalError.class,
                            LinkageError.class,
                            NoClassDefFoundError.class,
                            NoSuchFieldError.class,
                            NoSuchMethodError.class,
                            OutOfMemoryError.class,
                            StackOverflowError.class,
                            ThreadDeath.class,
                            UnknownError.class,
                            UnsatisfiedLinkError.class,
                            UnsupportedClassVersionError.class,
                            VerifyError.class,
                            VirtualMachineError.class)));

            put("java.lang.annotation", new ArrayList<Class>(Arrays.asList(
                    java.lang.annotation.ElementType.class,
                    java.lang.annotation.RetentionPolicy.class,
                    java.lang.annotation.AnnotationTypeMismatchException.class,
                    java.lang.annotation.IncompleteAnnotationException.class,
                    java.lang.annotation.AnnotationFormatError.class
            )));

            put("java.lang.ref", new ArrayList<Class>(Arrays.asList(
                    java.lang.ref.PhantomReference.class,
                    java.lang.ref.Reference.class,
                    java.lang.ref.ReferenceQueue.class,
                    java.lang.ref.SoftReference.class,
                    java.lang.ref.WeakReference.class
            )));

            put("java.lang.reflect", new ArrayList<Class>(Arrays.asList(
                    java.lang.reflect.AccessibleObject.class,
                    java.lang.reflect.Array.class,
                    java.lang.reflect.Constructor.class,
                    Field.class,
                    Method.class,
                    java.lang.reflect.Modifier.class,
                    java.lang.reflect.Proxy.class,
                    java.lang.reflect.ReflectPermission.class,
                    java.lang.reflect.InvocationTargetException.class,
                    java.lang.reflect.MalformedParameterizedTypeException.class,
                    java.lang.reflect.UndeclaredThrowableException.class,
                    java.lang.reflect.GenericSignatureFormatError.class
            )));

            put("java.math", new ArrayList<Class>(Arrays.asList(
                    java.math.BigDecimal.class,
                    java.math.BigInteger.class,
                    java.math.MathContext.class,
                    java.math.RoundingMode.class
            )));

            put("java.net", new ArrayList<Class>(Arrays.asList(
                    java.net.Authenticator.class,
                    java.net.CacheRequest.class,
                    java.net.CacheResponse.class,
                    java.net.ContentHandler.class,
                    java.net.CookieHandler.class,
                    java.net.CookieManager.class,
                    java.net.DatagramPacket.class,
                    java.net.DatagramSocket.class,
                    java.net.DatagramSocketImpl.class,
                    java.net.HttpCookie.class,
                    java.net.HttpURLConnection.class,
                    java.net.IDN.class,
                    java.net.Inet4Address.class,
                    java.net.Inet6Address.class,
                    java.net.InetAddress.class,
                    java.net.InetSocketAddress.class,
                    java.net.InterfaceAddress.class,
                    java.net.JarURLConnection.class,
                    java.net.MulticastSocket.class,
                    java.net.NetPermission.class,
                    java.net.NetworkInterface.class,
                    java.net.PasswordAuthentication.class,
                    java.net.Proxy.class,
                    java.net.ProxySelector.class,
                    java.net.ResponseCache.class,
                    java.net.SecureCacheResponse.class,
                    java.net.ServerSocket.class,
                    java.net.Socket.class,
                    java.net.SocketAddress.class,
                    java.net.SocketImpl.class,
                    java.net.SocketPermission.class,
                    java.net.StandardSocketOptions.class,
                    java.net.URI.class,
                    java.net.URL.class,
                    java.net.URLClassLoader.class,
                    java.net.URLConnection.class,
                    java.net.URLDecoder.class,
                    java.net.URLEncoder.class,
                    java.net.URLStreamHandler.class,
                    java.net.Authenticator.RequestorType.class,
                    java.net.Proxy.Type.class,
                    java.net.StandardProtocolFamily.class,
                    java.net.BindException.class,
                    java.net.ConnectException.class,
                    java.net.HttpRetryException.class,
                    java.net.MalformedURLException.class,
                    java.net.NoRouteToHostException.class,
                    java.net.PortUnreachableException.class,
                    java.net.ProtocolException.class,
                    java.net.SocketException.class,
                    java.net.SocketTimeoutException.class,
                    java.net.UnknownHostException.class,
                    java.net.UnknownServiceException.class,
                    java.net.URISyntaxException.class
            )));

            put("java.nio", new ArrayList<Class>(Arrays.asList(
                    java.nio.Buffer.class,
                    java.nio.ByteBuffer.class,
                    java.nio.ByteOrder.class,
                    java.nio.CharBuffer.class,
                    java.nio.DoubleBuffer.class,
                    java.nio.FloatBuffer.class,
                    java.nio.IntBuffer.class,
                    java.nio.LongBuffer.class,
                    java.nio.MappedByteBuffer.class,
                    java.nio.ShortBuffer.class,
                    java.nio.BufferOverflowException.class,
                    java.nio.BufferUnderflowException.class,
                    java.nio.InvalidMarkException.class,
                    java.nio.ReadOnlyBufferException.class
            )));

            put("java.nio.channels", new ArrayList<Class>(Arrays.asList(
                    java.nio.channels.Channels.class,
                    java.nio.channels.DatagramChannel.class,
                    java.nio.channels.FileChannel.class,
                    java.nio.channels.FileChannel.MapMode.class,
                    java.nio.channels.FileLock.class,
                    java.nio.channels.Pipe.class,
                    java.nio.channels.Pipe.SinkChannel.class,
                    java.nio.channels.Pipe.SourceChannel.class,
                    java.nio.channels.SelectableChannel.class,
                    java.nio.channels.SelectionKey.class,
                    java.nio.channels.Selector.class,
                    java.nio.channels.ServerSocketChannel.class,
                    java.nio.channels.SocketChannel.class,
                    java.nio.channels.AlreadyBoundException.class,
                    java.nio.channels.AlreadyConnectedException.class,
                    java.nio.channels.AsynchronousCloseException.class,
                    java.nio.channels.CancelledKeyException.class,
                    java.nio.channels.ClosedByInterruptException.class,
                    java.nio.channels.ClosedChannelException.class,
                    java.nio.channels.ClosedSelectorException.class,
                    java.nio.channels.ConnectionPendingException.class,
                    java.nio.channels.FileLockInterruptionException.class,
                    java.nio.channels.IllegalBlockingModeException.class,
                    java.nio.channels.IllegalSelectorException.class,
                    java.nio.channels.NoConnectionPendingException.class,
                    java.nio.channels.NonReadableChannelException.class,
                    java.nio.channels.NonWritableChannelException.class,
                    java.nio.channels.NotYetBoundException.class,
                    java.nio.channels.NotYetConnectedException.class,
                    java.nio.channels.OverlappingFileLockException.class,
                    java.nio.channels.UnresolvedAddressException.class,
                    java.nio.channels.UnsupportedAddressTypeException.class
            )));

            put("java.nio.channels.spi", new ArrayList<Class>(Arrays.asList(
                    java.nio.channels.spi.AbstractInterruptibleChannel.class,
                    java.nio.channels.spi.AbstractSelectableChannel.class,
                    java.nio.channels.spi.AbstractSelectionKey.class,
                    java.nio.channels.spi.AbstractSelector.class,
                    java.nio.channels.spi.SelectorProvider.class
            )));

            put("java.nio.charset", new ArrayList<Class>(Arrays.asList(
                    java.nio.charset.Charset.class,
                    java.nio.charset.CharsetDecoder.class,
                    java.nio.charset.CharsetEncoder.class,
                    java.nio.charset.CoderResult.class,
                    java.nio.charset.CodingErrorAction.class,
                    java.nio.charset.StandardCharsets.class,
                    java.nio.charset.CharacterCodingException.class,
                    java.nio.charset.IllegalCharsetNameException.class,
                    java.nio.charset.MalformedInputException.class,
                    java.nio.charset.UnmappableCharacterException.class,
                    java.nio.charset.UnsupportedCharsetException.class,
                    java.nio.charset.CoderMalfunctionError.class
            )));

            put("java.nio.charset.spi", new ArrayList<Class>(Arrays.asList(
                    java.nio.charset.spi.CharsetProvider.class
            )));

            put("java.security", new ArrayList<Class>(Arrays.asList(
                    java.security.AccessControlContext.class,
                    java.security.AccessController.class,
                    java.security.AlgorithmParameterGenerator.class,
                    java.security.AlgorithmParameterGeneratorSpi.class,
                    java.security.AlgorithmParameters.class,
                    java.security.AlgorithmParametersSpi.class,
                    java.security.AllPermission.class,
                    java.security.AuthProvider.class,
                    java.security.BasicPermission.class,
                    java.security.CodeSigner.class,
                    java.security.CodeSource.class,
                    java.security.DigestInputStream.class,
                    java.security.DigestOutputStream.class,
                    java.security.GuardedObject.class,
                    java.security.KeyFactory.class,
                    java.security.KeyFactorySpi.class,
                    java.security.KeyPair.class,
                    java.security.KeyPairGenerator.class,
                    java.security.KeyPairGeneratorSpi.class,
                    java.security.KeyRep.class,
                    java.security.KeyStore.class,
                    java.security.KeyStore.Builder.class,
                    java.security.KeyStore.CallbackHandlerProtection.class,
                    java.security.KeyStore.PasswordProtection.class,
                    java.security.KeyStore.PrivateKeyEntry.class,
                    java.security.KeyStore.SecretKeyEntry.class,
                    java.security.KeyStore.TrustedCertificateEntry.class,
                    java.security.KeyStoreSpi.class,
                    java.security.MessageDigest.class,
                    java.security.MessageDigestSpi.class,
                    java.security.Permission.class,
                    java.security.PermissionCollection.class,
                    java.security.Permissions.class,
                    java.security.Policy.class,
                    java.security.PolicySpi.class,
                    java.security.ProtectionDomain.class,
                    java.security.Provider.class,
                    java.security.Provider.Service.class,
                    java.security.SecureClassLoader.class,
                    java.security.SecureRandom.class,
                    java.security.SecureRandomSpi.class,
                    java.security.Security.class,
                    java.security.SecurityPermission.class,
                    java.security.Signature.class,
                    java.security.SignatureSpi.class,
                    java.security.SignedObject.class,
                    java.security.Timestamp.class,
                    java.security.UnresolvedPermission.class,
                    java.security.CryptoPrimitive.class,
                    java.security.KeyRep.Type.class,
                    java.security.AccessControlException.class,
                    java.security.DigestException.class,
                    java.security.GeneralSecurityException.class,
                    java.security.InvalidAlgorithmParameterException.class,
                    java.security.InvalidKeyException.class,
                    java.security.InvalidParameterException.class,
                    java.security.KeyException.class,
                    java.security.KeyManagementException.class,
                    java.security.KeyStoreException.class,
                    java.security.NoSuchAlgorithmException.class,
                    java.security.NoSuchProviderException.class,
                    java.security.PrivilegedActionException.class,
                    java.security.ProviderException.class,
                    java.security.SignatureException.class,
                    java.security.UnrecoverableEntryException.class,
                    java.security.UnrecoverableKeyException.class
            )));

            put("java.security.acl", new ArrayList<Class>(Arrays.asList(
                    java.security.acl.AclNotFoundException.class,
                    java.security.acl.LastOwnerException.class,
                    java.security.acl.NotOwnerException.class
            )));

            put("java.security.cert", new ArrayList<Class>(Arrays.asList(
                    java.security.cert.Certificate.class,
                    //java.security.cert.Certificate.CertificateRep.class,
                    java.security.cert.CertificateFactory.class,
                    java.security.cert.CertificateFactorySpi.class,
                    java.security.cert.CertPath.class,
                    //java.security.cert.CertPath.CertPathRep.class,
                    java.security.cert.CertPathBuilder.class,
                    java.security.cert.CertPathBuilderSpi.class,
                    java.security.cert.CertPathValidator.class,
                    java.security.cert.CertPathValidatorSpi.class,
                    java.security.cert.CertStore.class,
                    java.security.cert.CertStoreSpi.class,
                    java.security.cert.CollectionCertStoreParameters.class,
                    java.security.cert.CRL.class,
                    java.security.cert.LDAPCertStoreParameters.class,
                    java.security.cert.PKIXBuilderParameters.class,
                    java.security.cert.PKIXCertPathBuilderResult.class,
                    java.security.cert.PKIXCertPathChecker.class,
                    java.security.cert.PKIXCertPathValidatorResult.class,
                    java.security.cert.PKIXParameters.class,
                    java.security.cert.PKIXRevocationChecker.class,
                    java.security.cert.PolicyQualifierInfo.class,
                    java.security.cert.TrustAnchor.class,
                    java.security.cert.X509Certificate.class,
                    java.security.cert.X509CertSelector.class,
                    java.security.cert.X509CRL.class,
                    java.security.cert.X509CRLEntry.class,
                    java.security.cert.X509CRLSelector.class,
                    java.security.cert.CertPathValidatorException.BasicReason.class,
                    java.security.cert.CRLReason.class,
                    java.security.cert.PKIXReason.class,
                    java.security.cert.PKIXRevocationChecker.Option.class,
                    java.security.cert.CertificateEncodingException.class,
                    java.security.cert.CertificateException.class,
                    java.security.cert.CertificateExpiredException.class,
                    java.security.cert.CertificateNotYetValidException.class,
                    java.security.cert.CertificateParsingException.class,
                    java.security.cert.CertificateRevokedException.class,
                    java.security.cert.CertPathBuilderException.class,
                    java.security.cert.CertPathValidatorException.class,
                    java.security.cert.CertStoreException.class,
                    java.security.cert.CRLException.class
            )));

            put("java.security.spec", new ArrayList<Class>(Arrays.asList(
                    java.security.spec.DSAParameterSpec.class,
                    java.security.spec.DSAPrivateKeySpec.class,
                    java.security.spec.DSAPublicKeySpec.class,
                    java.security.spec.ECFieldF2m.class,
                    java.security.spec.ECFieldFp.class,
                    java.security.spec.ECGenParameterSpec.class,
                    java.security.spec.ECParameterSpec.class,
                    java.security.spec.ECPoint.class,
                    java.security.spec.ECPrivateKeySpec.class,
                    java.security.spec.ECPublicKeySpec.class,
                    java.security.spec.EllipticCurve.class,
                    java.security.spec.EncodedKeySpec.class,
                    java.security.spec.MGF1ParameterSpec.class,
                    java.security.spec.PKCS8EncodedKeySpec.class,
                    java.security.spec.PSSParameterSpec.class,
                    java.security.spec.RSAKeyGenParameterSpec.class,
                    java.security.spec.RSAMultiPrimePrivateCrtKeySpec.class,
                    java.security.spec.RSAOtherPrimeInfo.class,
                    java.security.spec.RSAPrivateCrtKeySpec.class,
                    java.security.spec.RSAPrivateKeySpec.class,
                    java.security.spec.RSAPublicKeySpec.class,
                    java.security.spec.X509EncodedKeySpec.class,
                    java.security.spec.InvalidKeySpecException.class,
                    java.security.spec.InvalidParameterSpecException.class
            )));

            put("java.sql", new ArrayList<Class>(Arrays.asList(
                    java.sql.Date.class,
                    java.sql.DriverManager.class,
                    java.sql.DriverPropertyInfo.class,
                    java.sql.SQLPermission.class,
                    java.sql.Time.class,
                    java.sql.Timestamp.class,
                    java.sql.Types.class,
                    java.sql.ClientInfoStatus.class,
                    java.sql.RowIdLifetime.class,
                    java.sql.BatchUpdateException.class,
                    java.sql.DataTruncation.class,
                    java.sql.SQLClientInfoException.class,
                    java.sql.SQLDataException.class,
                    java.sql.SQLException.class,
                    java.sql.SQLFeatureNotSupportedException.class,
                    java.sql.SQLIntegrityConstraintViolationException.class,
                    java.sql.SQLInvalidAuthorizationSpecException.class,
                    java.sql.SQLNonTransientConnectionException.class,
                    java.sql.SQLNonTransientException.class,
                    java.sql.SQLRecoverableException.class,
                    java.sql.SQLSyntaxErrorException.class,
                    java.sql.SQLTimeoutException.class,
                    java.sql.SQLTransactionRollbackException.class,
                    java.sql.SQLTransientConnectionException.class,
                    java.sql.SQLTransientException.class,
                    java.sql.SQLWarning.class
            )));

            put("java.text", new ArrayList<Class>(Arrays.asList(
                    java.text.Annotation.class,
                    java.text.AttributedCharacterIterator.Attribute.class,
                    java.text.AttributedString.class,
                    java.text.Bidi.class,
                    java.text.BreakIterator.class,
                    java.text.ChoiceFormat.class,
                    java.text.CollationElementIterator.class,
                    java.text.CollationKey.class,
                    java.text.Collator.class,
                    java.text.DateFormat.class,
                    java.text.DateFormat.Field.class,
                    java.text.DateFormatSymbols.class,
                    java.text.DecimalFormat.class,
                    java.text.DecimalFormatSymbols.class,
                    java.text.FieldPosition.class,
                    java.text.Format.class,
                    java.text.Format.Field.class,
                    java.text.MessageFormat.class,
                    java.text.MessageFormat.Field.class,
                    java.text.Normalizer.class,
                    java.text.NumberFormat.class,
                    java.text.NumberFormat.Field.class,
                    java.text.ParsePosition.class,
                    java.text.RuleBasedCollator.class,
                    java.text.SimpleDateFormat.class,
                    java.text.StringCharacterIterator.class,
                    java.text.Normalizer.Form.class,
                    java.text.ParseException.class
            )));

            put("java.util", new ArrayList<Class>(Arrays.asList(
                    AbstractCollection.class,
                    AbstractList.class,
                    AbstractMap.class,
                    SimpleEntry.class,
                    SimpleImmutableEntry.class,
                    AbstractQueue.class,
                    AbstractSequentialList.class,
                    AbstractSet.class,
                    ArrayDeque.class,
                    ArrayList.class,
                    Arrays.class,
                    Base64.class,
                    Base64.Decoder.class,
                    Base64.Encoder.class,
                    BitSet.class,
                    Calendar.class,
                    Calendar.Builder.class,
                    Collections.class,
                    Currency.class,
                    Date.class,
                    Dictionary.class,
                    DoubleSummaryStatistics.class,
                    EnumMap.class,
                    EnumSet.class,
                    EventListenerProxy.class,
                    EventObject.class,
                    FormattableFlags.class,
                    Formatter.class,
                    GregorianCalendar.class,
                    HashMap.class,
                    HashSet.class,
                    Hashtable.class,
                    IdentityHashMap.class,
                    IntSummaryStatistics.class,
                    LinkedHashMap.class,
                    LinkedHashSet.class,
                    LinkedList.class,
                    ListResourceBundle.class,
                    Locale.class,
                    Locale.Builder.class,
                    Locale.LanguageRange.class,
                    LongSummaryStatistics.class,
                    Objects.class,
                    Observable.class,
                    Optional.class,
                    OptionalDouble.class,
                    OptionalInt.class,
                    OptionalLong.class,
                    PriorityQueue.class,
                    Properties.class,
                    PropertyPermission.class,
                    PropertyResourceBundle.class,
                    Random.class,
                    ResourceBundle.class,
                    ResourceBundle.Control.class,
                    Scanner.class,
                    ServiceLoader.class,
                    SimpleTimeZone.class,
                    Spliterators.class,
                    Spliterators.AbstractDoubleSpliterator.class,
                    Spliterators.AbstractIntSpliterator.class,
                    Spliterators.AbstractLongSpliterator.class,
                    Spliterators.AbstractSpliterator.class,
                    SplittableRandom.class,
                    Stack.class,
                    StringJoiner.class,
                    StringTokenizer.class,
                    Timer.class,
                    TimerTask.class,
                    TimeZone.class,
                    TreeMap.class,
                    TreeSet.class,
                    UUID.class,
                    Vector.class,
                    WeakHashMap.class,
                    Formatter.BigDecimalLayoutForm.class,
                    Locale.Category.class,
                    Locale.FilteringMode.class,
                    ConcurrentModificationException.class,
                    DuplicateFormatFlagsException.class,
                    EmptyStackException.class,
                    FormatFlagsConversionMismatchException.class,
                    FormatterClosedException.class,
                    IllegalFormatCodePointException.class,
                    IllegalFormatConversionException.class,
                    IllegalFormatException.class,
                    IllegalFormatFlagsException.class,
                    IllegalFormatPrecisionException.class,
                    IllegalFormatWidthException.class,
                    IllformedLocaleException.class,
                    InputMismatchException.class,
                    InvalidPropertiesFormatException.class,
                    MissingFormatArgumentException.class,
                    MissingFormatWidthException.class,
                    MissingResourceException.class,
                    NoSuchElementException.class,
                    TooManyListenersException.class,
                    UnknownFormatConversionException.class,
                    UnknownFormatFlagsException.class,
                    ServiceConfigurationError.class
            )));

            put("java.util.concurrent", new ArrayList<Class>(Arrays.asList(
                    java.util.concurrent.AbstractExecutorService.class,
                    java.util.concurrent.ArrayBlockingQueue.class,
                    java.util.concurrent.CompletableFuture.class,
                    java.util.concurrent.ConcurrentHashMap.class,
                    java.util.concurrent.ConcurrentHashMap.KeySetView.class,
                    java.util.concurrent.ConcurrentLinkedDeque.class,
                    java.util.concurrent.ConcurrentLinkedQueue.class,
                    java.util.concurrent.ConcurrentSkipListMap.class,
                    java.util.concurrent.ConcurrentSkipListSet.class,
                    java.util.concurrent.CopyOnWriteArrayList.class,
                    java.util.concurrent.CopyOnWriteArraySet.class,
                    java.util.concurrent.CountDownLatch.class,
                    java.util.concurrent.CountedCompleter.class,
                    java.util.concurrent.CyclicBarrier.class,
                    java.util.concurrent.DelayQueue.class,
                    java.util.concurrent.Exchanger.class,
                    java.util.concurrent.ExecutorCompletionService.class,
                    java.util.concurrent.Executors.class,
                    java.util.concurrent.ForkJoinPool.class,
                    java.util.concurrent.ForkJoinTask.class,
                    java.util.concurrent.ForkJoinWorkerThread.class,
                    java.util.concurrent.FutureTask.class,
                    java.util.concurrent.LinkedBlockingDeque.class,
                    java.util.concurrent.LinkedBlockingQueue.class,
                    java.util.concurrent.LinkedTransferQueue.class,
                    java.util.concurrent.Phaser.class,
                    java.util.concurrent.PriorityBlockingQueue.class,
                    java.util.concurrent.RecursiveAction.class,
                    java.util.concurrent.RecursiveTask.class,
                    java.util.concurrent.ScheduledThreadPoolExecutor.class,
                    java.util.concurrent.Semaphore.class,
                    java.util.concurrent.SynchronousQueue.class,
                    java.util.concurrent.ThreadLocalRandom.class,
                    java.util.concurrent.ThreadPoolExecutor.class,
                    java.util.concurrent.ThreadPoolExecutor.AbortPolicy.class,
                    java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.class,
                    java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy.class,
                    java.util.concurrent.ThreadPoolExecutor.DiscardPolicy.class,
                    java.util.concurrent.TimeUnit.class,
                    java.util.concurrent.BrokenBarrierException.class,
                    java.util.concurrent.CancellationException.class,
                    java.util.concurrent.CompletionException.class,
                    java.util.concurrent.ExecutionException.class,
                    java.util.concurrent.RejectedExecutionException.class,
                    java.util.concurrent.TimeoutException.class
            )));

            put("java.util.concurrent.atomic", new ArrayList<Class>(Arrays.asList(
                    java.util.concurrent.atomic.AtomicBoolean.class,
                    java.util.concurrent.atomic.AtomicInteger.class,
                    java.util.concurrent.atomic.AtomicIntegerArray.class,
                    java.util.concurrent.atomic.AtomicIntegerFieldUpdater.class,
                    java.util.concurrent.atomic.AtomicLong.class,
                    java.util.concurrent.atomic.AtomicLongArray.class,
                    java.util.concurrent.atomic.AtomicLongFieldUpdater.class,
                    java.util.concurrent.atomic.AtomicMarkableReference.class,
                    java.util.concurrent.atomic.AtomicReference.class,
                    java.util.concurrent.atomic.AtomicReferenceArray.class,
                    java.util.concurrent.atomic.AtomicReferenceFieldUpdater.class,
                    java.util.concurrent.atomic.AtomicStampedReference.class,
                    java.util.concurrent.atomic.DoubleAccumulator.class,
                    java.util.concurrent.atomic.DoubleAdder.class,
                    java.util.concurrent.atomic.LongAccumulator.class,
                    java.util.concurrent.atomic.LongAdder.class
            )));

            put("java.util.concurrent.locks", new ArrayList<Class>(Arrays.asList(
                    java.util.concurrent.locks.AbstractOwnableSynchronizer.class,
                    java.util.concurrent.locks.AbstractQueuedLongSynchronizer.class,
                    java.util.concurrent.locks.AbstractQueuedSynchronizer.class,
                    java.util.concurrent.locks.LockSupport.class,
                    java.util.concurrent.locks.ReentrantLock.class,
                    java.util.concurrent.locks.ReentrantReadWriteLock.class,
                    java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock.class,
                    java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock.class,
                    java.util.concurrent.locks.StampedLock.class
            )));

            put("java.util.jar", new ArrayList<Class>(Arrays.asList(
                    java.util.jar.Attributes.class,
                    java.util.jar.Attributes.Name.class,
                    java.util.jar.JarEntry.class,
                    java.util.jar.JarFile.class,
                    java.util.jar.JarInputStream.class,
                    java.util.jar.JarOutputStream.class,
                    java.util.jar.Manifest.class,
                    java.util.jar.Pack200.class,
                    java.util.jar.JarException.class
            )));

            put("java.util.logging", new ArrayList<Class>(Arrays.asList(
                    java.util.logging.ConsoleHandler.class,
                    java.util.logging.ErrorManager.class,
                    java.util.logging.FileHandler.class,
                    java.util.logging.Formatter.class,
                    java.util.logging.Handler.class,
                    java.util.logging.Level.class,
                    java.util.logging.Logger.class,
                    java.util.logging.LoggingPermission.class,
                    java.util.logging.LogManager.class,
                    java.util.logging.LogRecord.class,
                    java.util.logging.MemoryHandler.class,
                    java.util.logging.SimpleFormatter.class,
                    java.util.logging.SocketHandler.class,
                    java.util.logging.StreamHandler.class,
                    java.util.logging.XMLFormatter.class
            )));

            put("java.util.prefs", new ArrayList<Class>(Arrays.asList(
                    java.util.prefs.AbstractPreferences.class,
                    java.util.prefs.NodeChangeEvent.class,
                    java.util.prefs.PreferenceChangeEvent.class,
                    java.util.prefs.Preferences.class,
                    java.util.prefs.BackingStoreException.class,
                    java.util.prefs.InvalidPreferencesFormatException.class
            )));

            put("java.util.regex", new ArrayList<Class>(Arrays.asList(
                    java.util.regex.Matcher.class,
                    java.util.regex.Pattern.class,
                    java.util.regex.PatternSyntaxException.class
            )));

            put("java.util.stream", new ArrayList<Class>(Arrays.asList(
                    java.util.stream.Collectors.class,
                    java.util.stream.StreamSupport.class,
                    java.util.stream.Collector.Characteristics.class
            )));

            put("java.util.zip", new ArrayList<Class>(Arrays.asList(
                    java.util.zip.Adler32.class,
                    java.util.zip.CheckedInputStream.class,
                    java.util.zip.CheckedOutputStream.class,
                    java.util.zip.CRC32.class,
                    java.util.zip.Deflater.class,
                    java.util.zip.DeflaterInputStream.class,
                    java.util.zip.DeflaterOutputStream.class,
                    java.util.zip.GZIPInputStream.class,
                    java.util.zip.GZIPOutputStream.class,
                    java.util.zip.Inflater.class,
                    java.util.zip.InflaterInputStream.class,
                    java.util.zip.InflaterOutputStream.class,
                    java.util.zip.ZipEntry.class,
                    java.util.zip.ZipFile.class,
                    java.util.zip.ZipInputStream.class,
                    java.util.zip.ZipOutputStream.class,
                    java.util.zip.DataFormatException.class,
                    java.util.zip.ZipException.class,
                    java.util.zip.ZipError.class
            )));
        }};
    }

    public static String clear(String string){
        string = string.replaceAll("\\.", "");
        string = string.replaceAll("\\_", "");
        return string.toLowerCase();
    }

    public static void addToLib(Class... instances){
        for (Class instance : instances) {
            if (!lib.containsKey(instance.getPackage().getName())){
                lib.put(instance.getPackage().getName(), new ArrayList<Class>());
            }
            lib.get(instance.getPackage().getName()).add(instance);
        }
    }

    public static List<SearchResult> getClassName(String name){
        List<SearchResult> found = new ArrayList<>();
        for (Map.Entry<String, List<Class>> packageEntry : lib.entrySet()) {
            for (Class instance : packageEntry.getValue()) {
                if (clear(instance.getName()).equals(name.toLowerCase())) {
                    found.add(new SearchResult(instance.getName(),
                            instance.getPackage().getName(), ResultType.CLASS).setClass(instance));
                }
            }
        }
        return found;
    }

    public static List<SearchResult> getInterfaceName(String name){
        List<SearchResult> found = new ArrayList<>();
        for (Map.Entry<String, List<Class>> packageEntry : lib.entrySet()) {
            for (Class instance : packageEntry.getValue()) {
                if (clear(instance.getName()).equals(name.toLowerCase())) {
                    found.add(new SearchResult(instance.getName(),
                            instance.getPackage().getName(), ResultType.INTERFACE).setClass(instance));
                }
            }
        }
        return found;
    }
    public static List<SearchResult> getFieldName(String name, String className){
        List<SearchResult> classes = getClassName(className);
        if (classes != null && !classes.isEmpty()) {
            List<SearchResult> found = new ArrayList<>();

            for (SearchResult result : classes) {
                if (result.instance != null) {
                    for (Field field : result.instance.getFields()){
                        if (clear(field.getName()).equals(name.toLowerCase())) {
                            found.add(new SearchResult(field.getName(), result.instance.getPackage().getName(), ResultType.FIELD)
                                    .setClass(result.instance));
                            break;
                        }
                    }
                }
            }

            return found;
        }
        return null;
    }

    public static List<SearchResult> getMethodName(String name, String className){
        List<SearchResult> classes = getClassName(className);
        if (classes != null && !classes.isEmpty()) {
            List<SearchResult> found = new ArrayList<>();

            for (SearchResult result : classes) {
                if (result.instance != null) {
                    for (Method method : result.instance.getMethods()){
                        if (clear(method.getName()).equals(name.toLowerCase())) {
                            found.add(new SearchResult(method.getName(), result.instance.getPackage().getName(), ResultType.METHOD)
                                    .setClass(result.instance));
                            break;
                        }
                    }
                }
            }

            return found;
        }
        return null;
    }
}