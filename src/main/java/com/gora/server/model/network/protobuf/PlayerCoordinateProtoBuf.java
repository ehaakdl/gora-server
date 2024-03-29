// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: PlayerCoordinate.proto

package com.gora.server.model.network.protobuf;

public final class PlayerCoordinateProtoBuf {
  private PlayerCoordinateProtoBuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface PlayerCoordinateOrBuilder extends
      // @@protoc_insertion_point(interface_extends:protobuf.PlayerCoordinate)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required float x = 1;</code>
     * @return Whether the x field is set.
     */
    boolean hasX();
    /**
     * <code>required float x = 1;</code>
     * @return The x.
     */
    float getX();

    /**
     * <code>required float y = 2;</code>
     * @return Whether the y field is set.
     */
    boolean hasY();
    /**
     * <code>required float y = 2;</code>
     * @return The y.
     */
    float getY();

    /**
     * <code>optional string key = 3;</code>
     * @return Whether the key field is set.
     */
    boolean hasKey();
    /**
     * <code>optional string key = 3;</code>
     * @return The key.
     */
    java.lang.String getKey();
    /**
     * <code>optional string key = 3;</code>
     * @return The bytes for key.
     */
    com.google.protobuf.ByteString
        getKeyBytes();
  }
  /**
   * Protobuf type {@code protobuf.PlayerCoordinate}
   */
  public static final class PlayerCoordinate extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:protobuf.PlayerCoordinate)
      PlayerCoordinateOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use PlayerCoordinate.newBuilder() to construct.
    private PlayerCoordinate(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private PlayerCoordinate() {
      key_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new PlayerCoordinate();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.internal_static_protobuf_PlayerCoordinate_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.internal_static_protobuf_PlayerCoordinate_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.class, com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.Builder.class);
    }

    private int bitField0_;
    public static final int X_FIELD_NUMBER = 1;
    private float x_ = 0F;
    /**
     * <code>required float x = 1;</code>
     * @return Whether the x field is set.
     */
    @java.lang.Override
    public boolean hasX() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required float x = 1;</code>
     * @return The x.
     */
    @java.lang.Override
    public float getX() {
      return x_;
    }

    public static final int Y_FIELD_NUMBER = 2;
    private float y_ = 0F;
    /**
     * <code>required float y = 2;</code>
     * @return Whether the y field is set.
     */
    @java.lang.Override
    public boolean hasY() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required float y = 2;</code>
     * @return The y.
     */
    @java.lang.Override
    public float getY() {
      return y_;
    }

    public static final int KEY_FIELD_NUMBER = 3;
    @SuppressWarnings("serial")
    private volatile java.lang.Object key_ = "";
    /**
     * <code>optional string key = 3;</code>
     * @return Whether the key field is set.
     */
    @java.lang.Override
    public boolean hasKey() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string key = 3;</code>
     * @return The key.
     */
    @java.lang.Override
    public java.lang.String getKey() {
      java.lang.Object ref = key_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          key_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string key = 3;</code>
     * @return The bytes for key.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getKeyBytes() {
      java.lang.Object ref = key_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        key_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasX()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasY()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeFloat(1, x_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeFloat(2, y_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, key_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(1, x_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(2, y_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, key_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate)) {
        return super.equals(obj);
      }
      com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate other = (com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate) obj;

      if (hasX() != other.hasX()) return false;
      if (hasX()) {
        if (java.lang.Float.floatToIntBits(getX())
            != java.lang.Float.floatToIntBits(
                other.getX())) return false;
      }
      if (hasY() != other.hasY()) return false;
      if (hasY()) {
        if (java.lang.Float.floatToIntBits(getY())
            != java.lang.Float.floatToIntBits(
                other.getY())) return false;
      }
      if (hasKey() != other.hasKey()) return false;
      if (hasKey()) {
        if (!getKey()
            .equals(other.getKey())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasX()) {
        hash = (37 * hash) + X_FIELD_NUMBER;
        hash = (53 * hash) + java.lang.Float.floatToIntBits(
            getX());
      }
      if (hasY()) {
        hash = (37 * hash) + Y_FIELD_NUMBER;
        hash = (53 * hash) + java.lang.Float.floatToIntBits(
            getY());
      }
      if (hasKey()) {
        hash = (37 * hash) + KEY_FIELD_NUMBER;
        hash = (53 * hash) + getKey().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code protobuf.PlayerCoordinate}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:protobuf.PlayerCoordinate)
        com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinateOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.internal_static_protobuf_PlayerCoordinate_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.internal_static_protobuf_PlayerCoordinate_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.class, com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.Builder.class);
      }

      // Construct using com.gora.server.model.network.PlayerCoordinateProtoBuf.PlayerCoordinate.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        x_ = 0F;
        y_ = 0F;
        key_ = "";
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.internal_static_protobuf_PlayerCoordinate_descriptor;
      }

      @java.lang.Override
      public com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate getDefaultInstanceForType() {
        return com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.getDefaultInstance();
      }

      @java.lang.Override
      public com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate build() {
        com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate buildPartial() {
        com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate result = new com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate result) {
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.x_ = x_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.y_ = y_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.key_ = key_;
          to_bitField0_ |= 0x00000004;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate) {
          return mergeFrom((com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate other) {
        if (other == com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate.getDefaultInstance()) return this;
        if (other.hasX()) {
          setX(other.getX());
        }
        if (other.hasY()) {
          setY(other.getY());
        }
        if (other.hasKey()) {
          key_ = other.key_;
          bitField0_ |= 0x00000004;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        if (!hasX()) {
          return false;
        }
        if (!hasY()) {
          return false;
        }
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 13: {
                x_ = input.readFloat();
                bitField0_ |= 0x00000001;
                break;
              } // case 13
              case 21: {
                y_ = input.readFloat();
                bitField0_ |= 0x00000002;
                break;
              } // case 21
              case 26: {
                key_ = input.readBytes();
                bitField0_ |= 0x00000004;
                break;
              } // case 26
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private float x_ ;
      /**
       * <code>required float x = 1;</code>
       * @return Whether the x field is set.
       */
      @java.lang.Override
      public boolean hasX() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>required float x = 1;</code>
       * @return The x.
       */
      @java.lang.Override
      public float getX() {
        return x_;
      }
      /**
       * <code>required float x = 1;</code>
       * @param value The x to set.
       * @return This builder for chaining.
       */
      public Builder setX(float value) {

        x_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>required float x = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearX() {
        bitField0_ = (bitField0_ & ~0x00000001);
        x_ = 0F;
        onChanged();
        return this;
      }

      private float y_ ;
      /**
       * <code>required float y = 2;</code>
       * @return Whether the y field is set.
       */
      @java.lang.Override
      public boolean hasY() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>required float y = 2;</code>
       * @return The y.
       */
      @java.lang.Override
      public float getY() {
        return y_;
      }
      /**
       * <code>required float y = 2;</code>
       * @param value The y to set.
       * @return This builder for chaining.
       */
      public Builder setY(float value) {

        y_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>required float y = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearY() {
        bitField0_ = (bitField0_ & ~0x00000002);
        y_ = 0F;
        onChanged();
        return this;
      }

      private java.lang.Object key_ = "";
      /**
       * <code>optional string key = 3;</code>
       * @return Whether the key field is set.
       */
      public boolean hasKey() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional string key = 3;</code>
       * @return The key.
       */
      public java.lang.String getKey() {
        java.lang.Object ref = key_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            key_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string key = 3;</code>
       * @return The bytes for key.
       */
      public com.google.protobuf.ByteString
          getKeyBytes() {
        java.lang.Object ref = key_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          key_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string key = 3;</code>
       * @param value The key to set.
       * @return This builder for chaining.
       */
      public Builder setKey(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        key_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>optional string key = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearKey() {
        key_ = getDefaultInstance().getKey();
        bitField0_ = (bitField0_ & ~0x00000004);
        onChanged();
        return this;
      }
      /**
       * <code>optional string key = 3;</code>
       * @param value The bytes for key to set.
       * @return This builder for chaining.
       */
      public Builder setKeyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        key_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:protobuf.PlayerCoordinate)
    }

    // @@protoc_insertion_point(class_scope:protobuf.PlayerCoordinate)
    private static final com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate();
    }

    public static com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<PlayerCoordinate>
        PARSER = new com.google.protobuf.AbstractParser<PlayerCoordinate>() {
      @java.lang.Override
      public PlayerCoordinate parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<PlayerCoordinate> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<PlayerCoordinate> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.gora.server.model.network.protobuf.PlayerCoordinateProtoBuf.PlayerCoordinate getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protobuf_PlayerCoordinate_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protobuf_PlayerCoordinate_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026PlayerCoordinate.proto\022\010protobuf\"5\n\020Pl" +
      "ayerCoordinate\022\t\n\001x\030\001 \002(\002\022\t\n\001y\030\002 \002(\002\022\013\n\003" +
      "key\030\003 \001(\tB9\n\035com.gora.server.model.netwo" +
      "rkB\030PlayerCoordinateProtoBuf"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_protobuf_PlayerCoordinate_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_protobuf_PlayerCoordinate_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protobuf_PlayerCoordinate_descriptor,
        new java.lang.String[] { "X", "Y", "Key", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
